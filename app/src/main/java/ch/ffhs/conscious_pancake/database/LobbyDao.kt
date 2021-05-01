package ch.ffhs.conscious_pancake.database

import ch.ffhs.conscious_pancake.vo.Lobby
import ch.ffhs.conscious_pancake.vo.Resource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class LobbyDao @Inject constructor() {

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
}