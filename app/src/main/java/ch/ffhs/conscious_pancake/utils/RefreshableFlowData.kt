package ch.ffhs.conscious_pancake.utils

import androidx.lifecycle.MutableLiveData
import ch.ffhs.conscious_pancake.vo.Resource

typealias RefreshFlowAction<T> = (suspend (start: Int, count: Long) -> T)

class RefreshableFlowData<T>(
    private val limit: Long = 10,
    private val refreshFlowAction: RefreshFlowAction<Resource<List<T>>>
) : MutableLiveData<Resource<List<T>>>() {
    private var currentIndex: Long = 0

    suspend fun refresh() {
        val newValue = refreshFlowAction(0, limit)
        currentIndex = limit
        postValue(newValue)
    }

    suspend fun next() {
        val newValue = refreshFlowAction(currentIndex.toInt(), limit)
        if (newValue.data != null) {
            currentIndex += newValue.data.size
        }
        val list: MutableList<T> = ArrayList()
        value?.data?.let { list.addAll(it) }
        newValue.data?.let { list.addAll(it) }
        val error = value?.message ?: newValue.message
        if (error != null) {
            postValue(Resource.error(error, list))
        } else {
            postValue(Resource.success(list))
        }
    }
}