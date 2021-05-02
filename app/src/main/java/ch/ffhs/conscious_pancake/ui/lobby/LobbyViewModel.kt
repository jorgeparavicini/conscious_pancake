package ch.ffhs.conscious_pancake.ui.lobby

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ffhs.conscious_pancake.repository.LobbyRepository
import ch.ffhs.conscious_pancake.vo.enums.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val lobbyRepo: LobbyRepository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val lobbyId: String
        get() = savedStateHandle.get<String>(LOBBY_ID_ARG_NAME)!!

    private val isHost: Boolean
        get() = savedStateHandle.get<Boolean>(IS_HOST_ARG_NAME)!!

    private val lobby = Transformations.map(lobbyRepo.getLiveLobby(lobbyId)) {
        if (it.status == Status.SUCCESS) {
            return@map it.data!!
        } else {
            Timber.e("Could not fetch lobby")
            return@map null
        }
    }

    fun leaveLobby() {
        if (isHost) {
            viewModelScope.launch {
                lobbyRepo.deleteLobby(lobbyId)
            }
        }
    }

    companion object {

        const val LOBBY_ID_ARG_NAME = "lobby_id"
        const val IS_HOST_ARG_NAME = "is_host"
    }
}