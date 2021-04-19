package ch.ffhs.conscious_pancake.ui.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ffhs.conscious_pancake.repository.GameRepository
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val gameRepo: GameRepository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String
        get() = savedStateHandle.get<String>(USER_ID_ARG_NAME)!!

    private val _games = gameRepo.getGames(userId)
    val games: LiveData<Resource<List<Game>>>
        get() = _games

    init {
        reloadGames()
    }

    fun reloadGames() {
        viewModelScope.launch {
            _games.refresh()
        }
    }

    fun loadMoreGames() {
        viewModelScope.launch {
            _games.next()
        }
    }

    companion object {

        const val USER_ID_ARG_NAME = "user_id"
    }
}