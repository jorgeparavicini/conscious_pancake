package ch.ffhs.conscious_pancake.utils

import androidx.lifecycle.MutableLiveData

// This approach assumes working with Callbacks.
// If the DAOs work with LiveData as well, we would need to extend MediatorLiveData
// and the refresh callback would call addSource

typealias RefreshAction<T> = (suspend () -> T)

class RefreshableLiveData<T>(private val refreshAction: RefreshAction<T>) : MutableLiveData<T>() {
    suspend fun refresh() {
        val newValue = refreshAction()
        postValue(newValue)
    }
}