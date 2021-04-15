package ch.ffhs.conscious_pancake.vo

import android.net.Uri
import androidx.databinding.Bindable
import ch.ffhs.conscious_pancake.BR
import com.google.firebase.firestore.Exclude

class User : BaseModel() {

    val imageChanged: Boolean
        get() = changes.containsKey(BR.profilePictureUri)

    @get:Bindable
    var username: String = ""
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.username)
        }

    @get:Bindable
    var profilePictureName: String = ""
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.profilePictureName)
        }

    @get:Bindable
    @get:Exclude
    var profilePictureUri: Uri? = null
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.profilePictureUri)
        }
}
