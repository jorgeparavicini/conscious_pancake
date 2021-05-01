package ch.ffhs.conscious_pancake.ui.host


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.ffhs.conscious_pancake.repository.LobbyRepository
import ch.ffhs.conscious_pancake.vo.Lobby
import ch.ffhs.conscious_pancake.vo.enums.Status
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HostGameViewModel @Inject constructor(private val lobbyRepo: LobbyRepository) : ViewModel() {

    val lobby = Lobby(Firebase.auth.currentUser!!.uid)

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun hostGame(callback: (lobby: Lobby) -> Unit) {
        viewModelScope.launch {
            val l = lobbyRepo.createLobby(lobby)

            if (l.status == Status.ERROR) {
                _errorMessage.value = l.message!!
            } else {
                withContext(Dispatchers.Main) {
                    callback(l.data!!)
                }
            }
        }
    }
}