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

    override suspend fun getFinishedGames(
        userId: String, lastDocKey: Timestamp?, count: Long
    ): Resource<List<Game>> {
        return getGamesFromSnapshot(userId, lastDocKey, count, true)
    }


    override suspend fun getInProgressGames(
        userId: String, lastDocKey: Timestamp?, count: Long
    ): Resource<List<Game>> {
        return getGamesFromSnapshot(userId, lastDocKey, count, false)
    }

    private suspend fun getGamesFromSnapshot(
        userId: String,
        lastDocKey: Timestamp?,
        count: Long,
        finished: Boolean,
    ): Resource<List<Game>> = suspendCancellableCoroutine { ctx ->
        val collection = Firebase.firestore.collection("games")
        val request =
            collection.whereArrayContains("players", userId)
                    .whereEqualTo("gameOver", finished)
                    .orderBy("lastAction")
                    .startAfter(lastDocKey)
                    .limit(count)

        request.get().addOnSuccessListener { querySnapshot ->
            val result = querySnapshot.map { Game.fromFirebase(it) }
            Timber.v("Fetched ${result.size} games for user: $userId, after snapshot ${lastDocKey}.")
            ctx.resume(Resource.success(result))
        }.addOnFailureListener {
            Timber.e("Failed fetching games for user: $userId. Exception: $it")
            ctx.resume(Resource.error("Could not fetch games."))
        }
    }

}