package ch.ffhs.conscious_pancake.database

import ch.ffhs.conscious_pancake.database.contracts.IHistoryDao
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.coroutines.resume

class HistoryDao @Inject constructor() : IHistoryDao {

    private val snapshots = ConcurrentHashMap<String, List<DocumentSnapshot>>()

    override suspend fun getHistoryForUser(
        uid: String, start: Int, count: Long
    ): Resource<List<Game>> = suspendCancellableCoroutine { ctx ->
        val collection = Firebase.firestore.collection("games")

        if (start > 0 && (snapshots[uid] == null || snapshots[uid]!!.size < start)) {
            throw IllegalStateException("Can not load more games for user, as last games were not loaded.")
        }
        snapshots[uid] = snapshots[uid]?.subList(0, start) ?: emptyList()
        val request =
            collection.whereArrayContains("players", uid)
                    .whereNotEqualTo("winner", null)
                    .orderBy("last_action")
                    .startAfter(snapshots[uid]?.lastOrNull()?.get("last_action"))
                    .limit(count)

        request.get().addOnSuccessListener { snapshot ->
            snapshots[uid] = snapshots[uid]!!.plus(snapshot.documents)
            val result = snapshot.map { Game.fromFirebase(it) }
            Timber.v("Fetched ${result.size} history games for user: $uid from $start to ${start + result.size}")
            ctx.resume(Resource.success(result))
        }.addOnFailureListener {
            Timber.e("Failed fetching history for user: $uid. Exception $it")
            ctx.resume(Resource.error("Could not fetch history."))
        }
    }
}