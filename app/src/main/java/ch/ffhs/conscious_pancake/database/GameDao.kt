package ch.ffhs.conscious_pancake.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.ffhs.conscious_pancake.database.contracts.IGameDao
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.RemoteMove
import ch.ffhs.conscious_pancake.vo.Resource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jorgeparavicini.draughts.model.core.Move
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class GameDao @Inject constructor() : IGameDao {

    private val collection get() = Firebase.firestore.collection("games")

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

    fun getLiveGame(gameId: String): LiveData<Resource<Game?>> {
        val doc = collection.document(gameId)
        val result = MutableLiveData<Resource<Game?>>()
        doc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Timber.e("Could not listen to game. Exception $error")
                result.value = Resource.error("Could not listen to game")
                return@addSnapshotListener
            }

            result.value =
                Resource.success(
                    if (snapshot != null && snapshot.exists()) Game.fromFirebase(snapshot) else null
                )
        }
        return result
    }

    suspend fun addMoveToGame(gameId: String, move: RemoteMove): Resource<Unit> =
        suspendCancellableCoroutine { ctx ->
            val doc = collection.document(gameId)
            Firebase.firestore.runTransaction { transaction ->
                val snapshot = transaction.get(doc)
                val game = Game.fromFirebase(snapshot)
                game.remoteMoves.add(move)
                transaction.set(doc, game)
            }.addOnSuccessListener {
                Timber.d("Successfully added move $move to game.")
                ctx.resume(Resource.success())
            }.addOnFailureListener {
                Timber.e("Could not add move to game. $it")
                ctx.resume(Resource.error("Could not add move to game"))
            }
        }

    private suspend fun getGamesFromSnapshot(
        userId: String,
        lastDocKey: Timestamp?,
        count: Long,
        finished: Boolean,
    ): Resource<List<Game>> = suspendCancellableCoroutine { ctx ->
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