package ch.ffhs.conscious_pancake.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.*
import ch.ffhs.conscious_pancake.repository.UserRepository
import ch.ffhs.conscious_pancake.utils.fileName
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.Status
import ch.ffhs.conscious_pancake.vo.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * The id of the user the connected view should display.
     */
    private val userId: String
        get() = savedStateHandle.get<String>(USER_ID_ARG_NAME)!!

    /**
     * If the currently logged in user is looking at its own profile enable editing.
     */
    val isEditable: Boolean
        get() = Firebase.auth.currentUser?.uid == userId

    private val _isEditing: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    /**
     * Should the editing layout be display or the view layout.
     */
    val isEditing: LiveData<Boolean>
        get() = _isEditing

    private val _errorMessage = MediatorLiveData<String>()

    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _user = userRepo.getUser(userId)

    /**
     * The user to be displayed
     */
    val user: LiveData<Resource<User>>
        get() = _user

    init {
        reloadUser()
    }

    fun toggleEditing() {
        _isEditing.value = !_isEditing.value!!
    }

    fun setProfilePicture(imageUri: Uri) {
        val u = user.value!!.data!!
        u.imageUUID = imageUri.fileName
        u.profilePictureUri = imageUri
    }

    fun saveChanges() {
        viewModelScope.launch {
            val update = userRepo.updateUser(userId, user.value!!.data!!)
            if (update.status == Status.ERROR && update.message != null) {
                _errorMessage.value = update.message!!
            }
            _isEditing.value = false
        }

    }

    fun reloadUser() {
        viewModelScope.launch {
            _user.refresh()
        }
    }

    companion object {
        const val USER_ID_ARG_NAME = "user_id"
    }
}