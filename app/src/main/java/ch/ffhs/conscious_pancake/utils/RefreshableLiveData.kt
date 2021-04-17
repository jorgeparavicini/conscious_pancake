package ch.ffhs.conscious_pancake.utils

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

// This approach assumes working with Callbacks.
// If the DAOs work with LiveData as well, we would need to extend MediatorLiveData
// and the refresh callback would call addSource

typealias RefreshAction<T> = (suspend () -> T)

class RefreshableLiveData<T>(private val refreshAction: RefreshAction<T>) : MutableLiveData<T>() {
    fun refresh(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val newValue = refreshAction()
            postValue(newValue)
        }
    }
}