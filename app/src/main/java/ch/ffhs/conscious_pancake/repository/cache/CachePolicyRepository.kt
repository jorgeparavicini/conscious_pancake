package ch.ffhs.conscious_pancake.repository.cache

import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.Status
import java.util.concurrent.ConcurrentHashMap

open class CachePolicyRepository<T> {

    protected val cache = ConcurrentHashMap<String, CacheEntry<T>>()

    protected suspend fun fetch(
        uid: String,
        cachePolicy: CachePolicy,
        cacheAction: ((String, T) -> Unit) = { key, value -> defaultCacheAction(key, value) },
        fetchRemote: (suspend () -> Resource<T>),
    ): Resource<T> {
        return when (cachePolicy.type) {
            CachePolicyType.NEVER -> fetchRemote()
            CachePolicyType.ALWAYS -> {
                return if (!cache.containsKey(uid)) fetchAndCache(uid, fetchRemote, cacheAction)
                else Resource.success(cache[uid]!!.value)
            }
            CachePolicyType.CLEAR -> {
                return if (cache.containsKey(uid)) {
                    val value = Resource.success(cache[uid]!!.value)
                    cache.remove(uid)
                    return value
                } else fetchRemote()
            }
            CachePolicyType.REFRESH -> fetchAndCache(uid, fetchRemote, cacheAction)
            CachePolicyType.EXPIRES -> {
                return if (cache.containsKey(uid) && (cache[uid]!!.timestamp + cachePolicy.expires) > System.currentTimeMillis()) Resource.success(
                    cache[uid]!!.value
                )
                else fetchAndCache(uid, fetchRemote, cacheAction)
            }
        }
    }

    private suspend fun fetchAndCache(
        uid: String, fetchRemote: (suspend () -> Resource<T>), cacheAction: (String, T) -> Unit
    ): Resource<T> {
        val value = fetchRemote()
        if (value.status == Status.SUCCESS) {
            cacheAction(uid, value.data!!)
        }
        return value
    }

    private fun defaultCacheAction(uid: String, value: T) {
        cache[uid] = CacheEntry(value)
    }
}