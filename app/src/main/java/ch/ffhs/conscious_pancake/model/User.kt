package ch.ffhs.conscious_pancake.model

import android.net.Uri
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ch.ffhs.conscious_pancake.BR
import com.google.firebase.firestore.Exclude

class User : BaseObservable() {

    @get:Bindable
    var isDirty: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                //notifyPropertyChanged(BR.isDirty)
            }
        }

    var imageChanged: Boolean = false
        private set

    @get:Bindable
    var username: String = ""
        set(value) {
            isDirty = true
            field = value
            notifyPropertyChanged(BR.username)
        }

    @get:Bindable
    var profilePictureName: String = ""
        set(value) {
            isDirty = true
            field = value
            notifyPropertyChanged(BR.profilePictureName)
        }

    @get:Bindable
    @get:Exclude
    var profilePictureUri: Uri? = null
        set(value) {
            imageChanged = true
            field = value
            notifyPropertyChanged(BR.profilePictureUri)
        }

    fun didSave() {
        isDirty = false
    }
}
