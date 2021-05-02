package ch.ffhs.conscious_pancake.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.ffhs.conscious_pancake.vo.Lobby
import ch.ffhs.conscious_pancake.vo.Resource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class LobbyDao @Inject constructor() {

    fun getLiveLobby(lobbyId: String): LiveData<Resource<Lobby?>> {
        val doc = Firebase.firestore.collection("lobbies").document(lobbyId)
        val result = MutableLiveData<Resource<Lobby?>>()
        doc.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Timber.e("Could not listen to lobby. Exception: $e")
                result.value = Resource.error("Could not listen to lobby")
                return@addSnapshotListener
            }

            result.value =
                Resource.success(if (snapshot != null) Lobby.fromFirebase(snapshot) else null)
        }
        return result
    }

    suspend fun createLobby(lobby: Lobby): Resource<Lobby> = suspendCancellableCoroutine { ctx ->
        val collection = Firebase.firestore.collection("lobbies")
        collection.add(lobby).addOnSuccessListener {
            Timber.i("Successfully created lobby: $lobby")
            lobby.id = it.id
            ctx.resume(Resource.success(lobby))
        }.addOnFailureListener {
            Timber.e("Failed to create lobby. $it")
            ctx.resume(Resource.error("Unable to create lobby"))
        }
    }

    suspend fun deleteLobby(lobbyId: String): Resource<Unit> = suspendCancellableCoroutine { ctx ->
        val doc = Firebase.firestore.collection("lobbies").document(lobbyId)
        doc.delete().addOnSuccessListener {
            Timber.d("Successfully deleted lobby $lobbyId")
            ctx.resume(Resource.success())
        }.addOnFailureListener {
            Timber.e("Could not delete lobby $lobbyId. Exception $it")
            ctx.resume(Resource.error("Could not delete lobby"))
        }
    }
}