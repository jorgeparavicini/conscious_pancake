package ch.ffhs.conscious_pancake.ui.lobby

import androidx.lifecycle.*
import ch.ffhs.conscious_pancake.repository.LobbyRepository
import ch.ffhs.conscious_pancake.repository.UserRepository
import ch.ffhs.conscious_pancake.repository.cache.CachePolicy
import ch.ffhs.conscious_pancake.repository.cache.CachePolicyType
import ch.ffhs.conscious_pancake.vo.Lobby
import ch.ffhs.conscious_pancake.vo.enums.Status
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val lobbyRepo: LobbyRepository,
    private val userRepo: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val lobbyId: String
        get() = savedStateHandle.get<String>(LOBBY_ID_ARG_NAME)!!

    private val isHost: Boolean
        get() = savedStateHandle.get<Boolean>(IS_HOST_ARG_NAME)!!

    private val _lobbyDestroyed = MutableLiveData(false)
    val lobbyDestroyed: LiveData<Boolean>
        get() = _lobbyDestroyed

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val lobby = Transformations.switchMap(lobbyRepo.getLiveLobby(lobbyId)) {
        Timber.i("Updated lobby")
        Timber.i(it.data?.gameId)
        if (it.status == Status.SUCCESS) {
            it.data?.let { l ->
                return@switchMap fetchUsersForLobby(l)
            } ?: run {
                Timber.i("Lobby closed by host")
                _lobbyDestroyed.value = true
                return@switchMap null
            }
        } else {
            Timber.e("Could not fetch lobby")
            return@switchMap null
        }
    }

    val canStartGame = Transformations.map(lobby) {
        it?.player2 != null && isHost
    }

    val gameStarted = Transformations.map(lobby) {
        it?.gameId
    }

    fun leaveLobby() {
        if (isHost) {
            viewModelScope.launch {
                lobbyRepo.deleteLobby(lobbyId)
            }
        } else {
            viewModelScope.launch {
                lobbyRepo.leaveLobby(lobbyId, Firebase.auth.currentUser!!.uid)
            }
        }
    }

    fun startGame() {
        if (isHost) {
            viewModelScope.launch {
                lobbyRepo.startGame(lobbyId).let {
                    if (it.status == Status.ERROR) {
                        _errorMessage.value = it.message!!
                    }
                }
            }
        } else {
            _errorMessage.value = "Only the host can start the game."
        }
    }

    private fun fetchUsersForLobby(lobby: Lobby): LiveData<Lobby> {
        val data = MutableLiveData(lobby)
        viewModelScope.launch {
            lobby.host = userRepo.getUser(lobby.hostId, CachePolicy(CachePolicyType.ALWAYS)).data
            lobby.player2Id?.let {
                lobby.player2 = userRepo.getUser(it, CachePolicy(CachePolicyType.ALWAYS)).data
            }
            data.value = lobby
        }
        return data
    }

    companion object {

        const val LOBBY_ID_ARG_NAME = "lobby_id"
        const val IS_HOST_ARG_NAME = "is_host"
    }
}