package ch.ffhs.conscious_pancake.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ch.ffhs.conscious_pancake.model.Resource
import ch.ffhs.conscious_pancake.model.User
import ch.ffhs.conscious_pancake.repository.UserRepository
import ch.ffhs.conscious_pancake.utils.fileName
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        const val UserIdArgName = "user_id"
    }

    private val userId: String get() = savedStateHandle.get<String>(UserIdArgName)!!
    val isEditable: Boolean get() = Firebase.auth.currentUser?.uid == userId

    private val _isEditing: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val isEditing: LiveData<Boolean> get() = _isEditing

    val user: LiveData<Resource<User>> = userRepo.getUser(userId)

    fun toggleEditing() {
        _isEditing.value = !_isEditing.value!!
    }

    fun setProfilePicture(imageUri: Uri) {
        val u = user.value!!.data!!
        u.profilePictureName = imageUri.fileName
        u.profilePictureUri = imageUri
    }

    fun saveChanges() {
        userRepo.updateUser(userId, user.value?.data!!)
    }
}