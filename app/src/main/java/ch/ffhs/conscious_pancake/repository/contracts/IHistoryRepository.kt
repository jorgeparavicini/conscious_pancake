package ch.ffhs.conscious_pancake.repository.contracts

import ch.ffhs.conscious_pancake.repository.cache.CachePolicy
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource

interface IHistoryRepository {

    suspend fun getHistory(uid: String, cachePolicy: CachePolicy, limit: Long): Resource<List<Game>>

    suspend fun getNextHistory(
        uid: String, cachePolicy: CachePolicy, limit: Long
    ): Resource<List<Game>>
}