package ch.ffhs.conscious_pancake.repository.cache

enum class CachePolicyType {

    /**
     * Fetch the latest data from the remote and return it without caching.
     */
    NEVER,

    /**
     * If a cached value exists for the given key, return that one and delete it from the cache.
     * Otherwise fetch it from the remote without caching it like [NEVER].
     */
    CLEAR,

    /**
     * If a cached value exists for the given key, return that one.
     * Otherwise fetch it from the remote, cache it and then return it.
     */
    ALWAYS,

    /**
     * If a cached value exists for the key and it is not older than the given timestamp, return it.
     * Otherwise fetch it from the remote, upsert the cache value, then return it.
     */
    EXPIRES,

    /**
     * Fetch the remove value no matter if a cached value exists, and upsert the cache value for it,
     * then return that value.
     */
    REFRESH
}