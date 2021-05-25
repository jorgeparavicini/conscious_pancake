package ch.ffhs.conscious_pancake.ui.join

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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinGameViewModel @Inject constructor(private val lobbyRepo: LobbyRepository, private val userRepo: UserRepository) : ViewModel() {

    private val limit: Long = 10

    private val _lobbies = MutableLiveData<List<Lobby>>()
    val lobbies: LiveData<List<Lobby>>
        get() = _lobbies

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _canLoadMore = MutableLiveData(false)
    val canLoadMore: LiveData<Boolean>
        get() = _canLoadMore

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    val hasLobbies: LiveData<Boolean>
        get() = Transformations.map(lobbies) {
            it.isNotEmpty()
        }

    val displayHint: LiveData<Boolean>
        get() = Transformations.map(lobbies) {
            it.isEmpty() && isLoading.value == false
        }

    var updatedRange = IntRange(0, 0)
        private set

    init {
        reloadLobbies(CachePolicyType.ALWAYS)
    }

    fun joinLobby(lobbyId: String, lobbyJoined: (lobbyId: String) -> Unit) {
        val user = Firebase.auth.currentUser!!.uid
        viewModelScope.launch {
            val request = lobbyRepo.joinLobby(lobbyId, user)
            if (request.status == Status.SUCCESS) {
                lobbyJoined(lobbyId)
            } else {
                _errorMessage.value = request.message!!
            }
        }
    }

    fun reloadLobbies(cachePolicyType: CachePolicyType = CachePolicyType.REFRESH) {
        _isLoading.value = true
        viewModelScope.launch {
            val request = lobbyRepo.getOpenLobbies(CachePolicy(cachePolicyType), limit)
            if (request.status != Status.SUCCESS) {
                request.message.let {
                    _errorMessage.value = it
                }
                return@launch
            }

            val data = request.data!!
            val lobbies = fetchUsersForLobbies(data)
            updateLobbies(lobbies, true)
        }
    }

    fun loadMoreLobbies() {
        _isLoading.value = true
        viewModelScope.launch {
            val request = lobbyRepo.getNextLobbies(CachePolicy(CachePolicyType.REFRESH), limit)
            if (request.status != Status.SUCCESS) {
                request.message.let {
                    _errorMessage.value = it
                }
            return@launch
            }

            val data = request.data!!
            val lobbies = fetchUsersForLobbies(data)
            updateLobbies(lobbies, false)
        }
    }

    private fun updateLobbies(newValue: List<Lobby>, didReload: Boolean = false) {
        val oldSize = lobbies.value?.size
        val newSize = newValue.size

        updatedRange = if (oldSize == null || newSize < oldSize)  {
            IntRange(0, newSize)
        } else {
            IntRange(oldSize, newSize)
        }

        _canLoadMore.value = if (didReload) {
            newSize >= limit
        } else {
            newSize - (oldSize ?: 0) >= limit
        }

        _lobbies.value = newValue
        _isLoading.value = false
    }

    private suspend fun fetchUsersForLobbies(lobbies: List<Lobby>) = coroutineScope {
        return@coroutineScope lobbies.map { lobby ->
            async {
                lobby.host = userRepo.getUser(lobby.hostId, CachePolicy(CachePolicyType.ALWAYS)).data
                lobby.player2Id?.let {
                    lobby.player2 = userRepo.getUser(it, CachePolicy(CachePolicyType.ALWAYS)).data
                }
                lobby
            }
        }.awaitAll()
    }

    fun consumeErrorMessage() {
        _errorMessage.value = null
    }
}