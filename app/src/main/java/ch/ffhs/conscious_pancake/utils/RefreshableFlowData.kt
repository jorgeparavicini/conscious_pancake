package ch.ffhs.conscious_pancake.utils

import androidx.lifecycle.MutableLiveData
import ch.ffhs.conscious_pancake.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias RefreshFlowAction<T> = (suspend (start: Int, count: Long) -> T)

// TODO: Works but could be written a lot better. Needs refactoring
class RefreshableFlowData<T>(
    private val limit: Long = 10,
    private val refreshFlowAction: RefreshFlowAction<Resource<List<T>>>
) : MutableLiveData<Resource<List<T>>>() {

    private var currentIndex: Long = 0
    var updatedRange: IntRange = 0..0
        private set

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val newValue = refreshFlowAction(0, limit)
        currentIndex = limit
        updateRange(null, newValue.data)
        postValue(newValue)
    }

    suspend fun next() = withContext(Dispatchers.IO) {
        val newValue = refreshFlowAction(currentIndex.toInt(), limit)
        if (newValue.data != null) {
            currentIndex += newValue.data.size
        }
        val list: MutableList<T> = ArrayList()
        value?.data?.let { list.addAll(it) }
        newValue.data?.let { list.addAll(it) }
        val error = value?.message ?: newValue.message

        updateRange(value?.data, list)
        if (error != null) {
            postValue(Resource.error(error, list))
        } else {
            postValue(Resource.success(list))
        }
    }

    private fun updateRange(oldValue: List<*>?, newList: List<*>?) {
        val start = oldValue?.size ?: 0
        val end = (newList?.size ?: 0)
        updatedRange = start..end
    }
}