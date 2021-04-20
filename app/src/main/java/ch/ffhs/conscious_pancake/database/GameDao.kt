package ch.ffhs.conscious_pancake.database

import ch.ffhs.conscious_pancake.database.contracts.IGameDao
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.coroutines.resume

class GameDao @Inject constructor() : IGameDao {

    private val snapshots = ConcurrentHashMap<String, List<DocumentSnapshot>>()

    override suspend fun getGamesForUser(
        uid: String, start: Int, count: Long
    ): Resource<List<Game>> = suspendCancellableCoroutine { ctx ->
        val collection = Firebase.firestore.collection("games")
        // Trunk the snapshots for the user at the start index.
        // Since the user requested snapshots from start to start + count,
        // everything after start is garbage.
        if (start > 0 && (snapshots[uid] == null || snapshots[uid]!!.size < start)) {
            throw IllegalStateException("Can not get next game page as last was not loaded.")
        }
        snapshots[uid] = snapshots[uid]?.subList(0, start) ?: emptyList()

        // We can create the request now where start after points
        // to the last object in the snapshots that was previously truncated.
        // It can be null as that would just take the first one, which is the expected behaviour
        val request =
            collection.whereArrayContains("players", uid)
                    .orderBy("last_action")
                    .startAfter(snapshots[uid]?.lastOrNull()?.get("last_action"))
                    .limit(count)

        request.get().addOnSuccessListener { snapshot ->
            snapshots[uid] = snapshots[uid]!!.plus(snapshot.documents)
            val result = snapshot.map { snapshotToGame(it) }
            Timber.v("Fetched ${result.size} games for user: $uid from $start to ${start + result.size}")
            ctx.resume(Resource.success(result))
        }.addOnFailureListener {
            Timber.e("Failed fetching games for user: $uid. Exception: $it")
            ctx.resume(Resource.error("Could not fetch games."))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun snapshotToGame(snapshot: QueryDocumentSnapshot): Game {
        val obj = Game()
        obj.id = snapshot.id
        obj.lastAction = snapshot.getTimestamp("last_action")!!
        obj.turn = snapshot.getLong("turn")!!.toInt()
        obj.winner = snapshot.getString("winner")
        val players = snapshot.get("players") as List<String>
        obj.player1Id = players[0]
        obj.player2Id = players[1]
        return obj
    }
}