package ch.ffhs.conscious_pancake.ui.history

import androidx.lifecycle.*
import ch.ffhs.conscious_pancake.repository.HistoryRepository
import ch.ffhs.conscious_pancake.ui.lobby.LobbyViewModel
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    historyRepo: HistoryRepository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val limit: Long = 1

    private val userId: String
        get() = savedStateHandle.get<String>(LobbyViewModel.USER_ID_ARG_NAME)!!

    private val _history = historyRepo.getHistory(userId, limit)
    val history: LiveData<Resource<List<Game>>>
        get() = _history

    val updatedRange: IntRange
        get() = _history.updatedRange

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    val hasGames: LiveData<Boolean>
        get() = Transformations.map(history) {
            it.data != null && it.data.isNotEmpty()
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
            _history.refresh()
            updateCanLoadMore()
        }
    }

    fun loadMoreGames() {
        _isLoading.value = true
        viewModelScope.launch {
            _history.next()
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