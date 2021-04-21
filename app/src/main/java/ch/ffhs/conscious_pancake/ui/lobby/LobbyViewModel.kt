package ch.ffhs.conscious_pancake.ui.lobby

import androidx.lifecycle.*
import ch.ffhs.conscious_pancake.repository.GameRepository
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    gameRepo: GameRepository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val limit: Long = 1

    private val userId: String
        get() = savedStateHandle.get<String>(USER_ID_ARG_NAME)!!

    private val _games = gameRepo.getGames(userId, limit)
    val games: LiveData<Resource<List<Game>>>
        get() = _games

    val updatedRange: IntRange
        get() = _games.updatedRange

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    val hasGames: LiveData<Boolean>
        get() = Transformations.map(games) {
            it.data != null && it.data.isNotEmpty()
        }

    val displayHint: LiveData<Boolean>
        get() = Transformations.map(games) {
            it.data != null && it.data.isEmpty()
        }

    private val _canLoadMore = MutableLiveData(false)
    val canLoadMore: LiveData<Boolean>
        get() = _canLoadMore

    init {
        reloadGames()
    }

    fun reloadGames() {
        _isLoading.value = true
        viewModelScope.launch {
            _games.refresh()
            updateCanLoadMore()
        }
    }

    fun loadMoreGames() {
        _isLoading.value = true
        viewModelScope.launch {
            _games.next()
            updateCanLoadMore()
        }
    }

    private fun updateCanLoadMore() {
        _isLoading.value = false
        _canLoadMore.value = (updatedRange.last - updatedRange.first) >= limit
    }

    companion object {

        const val USER_ID_ARG_NAME = "user_id"
    }
}