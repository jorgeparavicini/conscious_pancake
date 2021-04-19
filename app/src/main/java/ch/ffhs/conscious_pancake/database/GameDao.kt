package ch.ffhs.conscious_pancake.database

import ch.ffhs.conscious_pancake.database.contracts.IGameDao
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class GameDao @Inject constructor() : IGameDao {

    @Suppress("UNCHECKED_CAST")
    override suspend fun getGamesForUser(
        uid: String, start: Int, count: Long
    ): Resource<List<Game>> = suspendCancellableCoroutine { ctx ->
        val collection = Firebase.firestore.collection("games")
        val request =
            collection.whereArrayContains("players", uid)
                    .orderBy("last_action")
                    .startAt(start)
                    .limit(count)

        request.get().addOnSuccessListener { snapshot ->
            val result = snapshot.map {
                val obj = Game()
                obj.id = it.id
                obj.lastAction = it.getTimestamp("last_action")!!
                obj.turn = it.getLong("turn")!!.toInt()
                obj.winner = it.getString("winner")
                val players = it.get("players") as List<String>
                obj.player1Id = players[0]
                obj.player2Id = players[1]
                obj
            }
            Timber.v("Fetched ${result.size} games for user: $uid from $start to ${start + result.size}")
            ctx.resume(Resource.success(result))
        }.addOnFailureListener {
            Timber.e("Failed fetching games for user: $uid. Exception: $it")
            ctx.resume(Resource.error("Could not fetch games."))
        }
    }

}