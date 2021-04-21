package ch.ffhs.conscious_pancake.vo

import android.net.Uri
import androidx.databinding.Bindable
import ch.ffhs.conscious_pancake.BR
import com.google.firebase.firestore.Exclude

class User : BaseModel() {

    val imageChanged: Boolean
        @Exclude get() = changes.containsKey(BR.profilePictureUri)

    @get:Bindable
    var username: String = ""
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.username)
        }

    @get:Bindable
    var firstName: String = ""
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.firstName)
        }

    @get:Bindable
    var lastName: String = ""
    set(value) {
        val old = field
        field = value
        applyChanges(old, value, BR.lastName)
    }

    @get:Bindable
    var imageUUID: String? = null
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.imageUUID)
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
