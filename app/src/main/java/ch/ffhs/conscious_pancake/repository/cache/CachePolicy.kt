package ch.ffhs.conscious_pancake.repository.cache

data class CachePolicy(
    val type: CachePolicyType = CachePolicyType.ALWAYS, val expires: Long = 0
)