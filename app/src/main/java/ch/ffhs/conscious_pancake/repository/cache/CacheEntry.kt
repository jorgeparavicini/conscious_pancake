package ch.ffhs.conscious_pancake.repository.cache

data class CacheEntry<T>(
    val value: T, val timestamp: Long = System.currentTimeMillis()
)