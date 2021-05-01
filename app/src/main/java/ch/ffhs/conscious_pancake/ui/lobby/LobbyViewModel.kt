package ch.ffhs.conscious_pancake.ui.lobby

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import ch.ffhs.conscious_pancake.repository.LobbyRepository
import ch.ffhs.conscious_pancake.repository.UserRepository
import ch.ffhs.conscious_pancake.repository.cache.CachePolicy
import ch.ffhs.conscious_pancake.repository.cache.CachePolicyType
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.Status
import com.google.android.gms.common.UserRecoverableException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val lobbyRepo: LobbyRepository,
    private val userRepo: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val limit: Long = 10

    private val userId: String
        get() = savedStateHandle.get<String>(USER_ID_ARG_NAME)!!

    private val _lobbies = MutableLiveData<List<Game>>()
    val lobbies: LiveData<List<Game>>
        get() = _lobbies

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _canLoadMore = MutableLiveData(false)
    val canLoadMore: LiveData<Boolean>
        get() = _canLoadMore

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val hasGames: LiveData<Boolean>
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
        reloadGames(CachePolicyType.ALWAYS)
    }

    // TODO: reload games and load more games can be refactored with callback.
    fun reloadGames(cachePolicyType: CachePolicyType = CachePolicyType.REFRESH) {
        _isLoading.value = true
        viewModelScope.launch {
            val request = lobbyRepo.getLobbies(userId, CachePolicy(cachePolicyType), limit)
            if (request.status != Status.SUCCESS) {
                request.message.let {
                    _errorMessage.value = it
                }
                return@launch
            }

            val data = request.data!!
            val games = fetchUsersForLobbies(data)
            updateLobbies(games, true)
        }
    }

    fun loadMoreGames() {
        _isLoading.value = true
        viewModelScope.launch {
            val request =
                lobbyRepo.getNextLobbies(userId, CachePolicy((CachePolicyType.REFRESH)), limit)
            if (request.status != Status.SUCCESS) {
                request.message.let {
                    _errorMessage.value = it
                }
                return@launch
            }

            val data = request.data!!
            val games = fetchUsersForLobbies(data)
            updateLobbies(games)
        }
    }

    private fun updateLobbies(newValue: List<Game>, didReload: Boolean = false) {
        val oldSize = lobbies.value?.size
        val newSize = newValue.size

        // Update updated range
        updatedRange = if (oldSize == null || newSize < oldSize) {
            IntRange(0, newSize)
        } else {
            IntRange(oldSize, newSize)
        }

        // Update can load more
        _canLoadMore.value = newSize - (oldSize ?: 0) >= limit || didReload

        _lobbies.value = newValue
        _isLoading.value = false
    }

    private suspend fun fetchUsersForLobbies(games: List<Game>) = coroutineScope {
        return@coroutineScope games.map { game ->
            async {
                game.player1 =
                    userRepo.getUser(game.player1Id, CachePolicy(CachePolicyType.ALWAYS)).data
                game.player2 =
                    userRepo.getUser(game.player2Id, CachePolicy(CachePolicyType.ALWAYS)).data
                game
            }
        }.awaitAll()
    }

    companion object {

        const val USER_ID_ARG_NAME = "user_id"
    }
}