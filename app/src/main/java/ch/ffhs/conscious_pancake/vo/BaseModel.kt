package ch.ffhs.conscious_pancake.vo

import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Exclude

open class BaseModel : BaseObservable() {

    /**
     * Should changes be tracked.
     * Usually will always be on except when working with DTOs
     * When filling up the DTO in the DAL we don't want to track the changes.
     */
    private var trackChanges = false

    /**
     * A record of the changes made to the object
     */
    @get:Exclude
    val changes = HashMap<Int, Any?>()

    private val _isDirty = MutableLiveData(false)

    /**
     * Have there been made changes to the object.
     */
    val isDirty: LiveData<Boolean>
        @Exclude get() = _isDirty

    fun <T> applyChanges(old: T?, new: T?, fieldId: Int) {
        if (old != new) {
            if (trackChanges) {
                changes[fieldId] = new
                updateIsDirty()
            }
            notifyPropertyChanged(fieldId)
        }
    }

    /**
     * Reset all changes and mark the object as not dirty.
     */
    fun reset() {
        changes.clear()
        updateIsDirty()
    }

    private fun updateIsDirty() {
        _isDirty.value = changes.isNotEmpty()
    }

    /**
     * Start tracking changes
     */
    fun startTracking() {
        trackChanges = true
    }

    /**
     * Stop tracking changes
     */
    fun stopTracking() {
        trackChanges = false
    }
}