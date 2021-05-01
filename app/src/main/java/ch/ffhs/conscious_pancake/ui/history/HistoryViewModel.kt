package ch.ffhs.conscious_pancake.ui.history

import androidx.lifecycle.*
import ch.ffhs.conscious_pancake.repository.HistoryRepository
import ch.ffhs.conscious_pancake.repository.UserRepository
import ch.ffhs.conscious_pancake.repository.cache.CachePolicy
import ch.ffhs.conscious_pancake.repository.cache.CachePolicyType
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: This view model could be refactored into a generic one with the Lobby view model

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepo: HistoryRepository,
    private val userRepo: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val limit: Long = 10

    private val userId: String
        get() = savedStateHandle.get<String>(USER_ID_ARG_NAME)!!

    private val _history = MutableLiveData<List<Game>>()
    val history: LiveData<List<Game>>
        get() = _history

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
        get() = Transformations.map(history) {
            it.isNotEmpty()
        }

    val displayHint: LiveData<Boolean>
        get() = Transformations.map(history) {
            it.isEmpty() && isLoading.value == false
        }

    var updatedRange = IntRange(0, 0)
        private set

    init {
        reloadGames(CachePolicyType.ALWAYS)
    }

    fun reloadGames(cachePolicyType: CachePolicyType = CachePolicyType.REFRESH) {
        _isLoading.value = true
        viewModelScope.launch {
            val request = historyRepo.getHistory(userId, CachePolicy(cachePolicyType), limit)
            if (request.status != Status.SUCCESS) {
                request.message.let {
                    _errorMessage.value = it
                }
                return@launch
            }

            val data = request.data!!
            val games = fetchUsersForGames(data)
            updateGames(games, true)
        }
    }

    fun loadMoreGames() {
        _isLoading.value = true
        viewModelScope.launch {
            val request =
                historyRepo.getHistory(userId, CachePolicy(CachePolicyType.REFRESH), limit)
            if (request.status != Status.SUCCESS) {
                request.message.let {
                    _errorMessage.value = it
                }
                return@launch
            }

            val data = request.data!!
            val games = fetchUsersForGames(data)
            updateGames(games)
        }
    }

    private fun updateGames(newValue: List<Game>, didReload: Boolean = false) {
        val oldSize = history.value?.size
        val newSize = newValue.size

        updatedRange = if (oldSize == null || newSize < oldSize) {
            IntRange(0, newSize)
        } else {
            IntRange(oldSize, newSize)
        }

        _canLoadMore.value = if (didReload) {
            newSize >= limit
        } else {
            newSize - (oldSize ?: 0) >= limit
        }

        _history.value = newValue
        _isLoading.value = false
    }

    // TODO: this should not be in this view model. Maybe add to User repo?
    // Same exact function as in lobby view model needs refactoring
    private suspend fun fetchUsersForGames(games: List<Game>) = coroutineScope {
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