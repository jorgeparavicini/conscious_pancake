package ch.ffhs.conscious_pancake.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Lobby
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.enums.PartyType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class LobbyDao @Inject constructor() {

    private val collection get() = Firebase.firestore.collection("lobbies")

    fun getLiveLobby(lobbyId: String): LiveData<Resource<Lobby?>> {
        val doc = collection.document(lobbyId)
        val result = MutableLiveData<Resource<Lobby?>>()
        doc.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Timber.e("Could not listen to lobby. Exception: $e")
                result.value = Resource.error("Could not listen to lobby")
                return@addSnapshotListener
            }

            result.value = Resource.success(
                if (snapshot != null && snapshot.exists()) Lobby.fromFirebase(snapshot) else null
            )
        }
        return result
    }

    suspend fun getOpenLobbies(lastDocKey: Timestamp?, limit: Long): Resource<List<Lobby>> =
        suspendCancellableCoroutine { ctx ->
            val request =
                collection.whereEqualTo("partyType", PartyType.OPEN.name)
                        .orderBy("createdAt")
                        .startAfter(lastDocKey)
                        .limit(limit)

            request.get().addOnSuccessListener { snapshot ->
                val result = snapshot.map { Lobby.fromFirebase(it) }
                Timber.v("Fetched ${result.size} open lobbies after snapshot $lastDocKey")
                ctx.resume(Resource.success(result))
            }.addOnFailureListener {
                Timber.e("Failed fetching open lobbies. Exception: $it")
                ctx.resume(Resource.error("Could not fetch open lobbies"))
            }
        }

    suspend fun createLobby(lobby: Lobby): Resource<Lobby> = suspendCancellableCoroutine { ctx ->
        collection.add(lobby).addOnSuccessListener {
            Timber.i("Successfully created lobby: ${it.id}")
            lobby.id = it.id
            ctx.resume(Resource.success(lobby))
        }.addOnFailureListener {
            Timber.e("Failed to create lobby. $it")
            ctx.resume(Resource.error("Unable to create lobby"))
        }
    }

    suspend fun joinLobby(lobbyId: String, userId: String): Resource<Unit> =
        suspendCancellableCoroutine { ctx ->
            if (lobbyId.isEmpty()) {
                ctx.resume(Resource.error("Please provide a lobby id"))
                return@suspendCancellableCoroutine
            }
            val doc = collection.document(lobbyId)
            Firebase.firestore.runTransaction { transaction ->
                val snapshot = transaction.get(doc)

                if (snapshot.getString("hostId") == userId) {
                    return@runTransaction "Can not join your own lobby"
                }

                snapshot.getString("player2Id")?.let {
                    return@runTransaction "Could not join the lobby. Lobby already full"
                } ?: run {
                    Timber.v("User $userId trying to join lobby $lobbyId")
                    transaction.update(doc, "player2Id", userId)
                }

                null
            }.addOnSuccessListener {
                it?.let {
                    Timber.e(it)
                    ctx.resume(Resource.error(it))
                } ?: run {
                    Timber.i("User $userId successfully joined lobby $lobbyId")
                    ctx.resume(Resource.success())
                }
            }.addOnFailureListener {
                Timber.e("Failed to add user $userId to lobby $lobbyId. Exception: $it")
                ctx.resume(Resource.error("Could not join the lobby."))
            }
        }

    suspend fun leaveLobby(lobbyId: String, userId: String): Resource<Unit> =
        suspendCancellableCoroutine { ctx ->
            if (lobbyId.isEmpty()) {
                ctx.resume(Resource.error("Please provide a lobby id"))
                return@suspendCancellableCoroutine
            }

            val doc = collection.document(lobbyId)
            Firebase.firestore.runTransaction { transaction ->
                val snapshot = transaction.get(doc)

                snapshot.getString("player2Id")?.let {
                    if (it == userId) {
                        Timber.v("User $userId leaving lobby $lobbyId")
                        transaction.update(doc, "player2Id", null)
                    } else {
                        return@runTransaction "Player is not in this lobby"
                    }
                } ?: return@runTransaction "No player in this lobby"

                null
            }.addOnSuccessListener {
                it?.let {
                    Timber.e(it)
                    ctx.resume(Resource.error(it))
                } ?: run {
                    Timber.i("User $userId successfully left lobby $lobbyId")
                    ctx.resume(Resource.success())
                }
            }.addOnFailureListener {
                Timber.e("User $userId was unable to leave lobby $lobbyId. Exception $it")
                ctx.resume(Resource.error("Could not leave lobby"))
            }
        }

    suspend fun deleteLobby(lobbyId: String): Resource<Unit> = suspendCancellableCoroutine { ctx ->
        val doc = collection.document(lobbyId)
        doc.delete().addOnSuccessListener {
            Timber.d("Successfully deleted lobby $lobbyId")
            ctx.resume(Resource.success())
        }.addOnFailureListener {
            Timber.e("Could not delete lobby $lobbyId. Exception $it")
            ctx.resume(Resource.error("Could not delete lobby"))
        }
    }

    suspend fun startGame(lobbyId: String): Resource<Unit> = suspendCancellableCoroutine { ctx ->
        if (lobbyId.isEmpty()) {
            ctx.resume(Resource.error("Please provide a lobby id"))
            return@suspendCancellableCoroutine
        }
        val doc = collection.document(lobbyId)
        Firebase.firestore.runTransaction { transaction ->
            val snapshot = transaction.get(doc)

            if (snapshot.getString("gameId") != null) {
                return@runTransaction "Lobby already has associated game"
            }

            val game = Game().apply {
                hostId = snapshot.getString("hostId")!!
                player2Id =
                    snapshot.getString("player2Id")
                        ?: return@runTransaction "Need a second player to start the game"
            }

            Timber.v("Creating game instance for lobby $lobbyId")
            val gameDoc = Firebase.firestore.collection("games").document()
            transaction.set(gameDoc, game)

            Timber.v("Created game ${gameDoc.id}")
            transaction.update(doc, "gameId", gameDoc.id)

            return@runTransaction null
        }.addOnSuccessListener {
            it?.let {
                Timber.e(it)
                ctx.resume(Resource.error(it))
            } ?: run {
                Timber.i("Successfully assigned game to lobby $lobbyId")
                ctx.resume(Resource.success())
            }
        }.addOnFailureListener {
            Timber.e("Unable to assign game to lobby $lobbyId. Exception $it")
            ctx.resume(Resource.error("Unable to initiate game"))
        }
    }
}