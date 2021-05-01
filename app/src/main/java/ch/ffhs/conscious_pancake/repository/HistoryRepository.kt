package ch.ffhs.conscious_pancake.repository

import ch.ffhs.conscious_pancake.database.GameDao
import ch.ffhs.conscious_pancake.repository.cache.CachePolicy
import ch.ffhs.conscious_pancake.repository.cache.CachePolicyRepository
import ch.ffhs.conscious_pancake.repository.contracts.IHistoryRepository
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val gameDao: GameDao
) : CachePolicyRepository<List<Game>>(), IHistoryRepository {

    override suspend fun getHistory(
        uid: String, cachePolicy: CachePolicy, limit: Long
    ): Resource<List<Game>> = withContext(Dispatchers.IO) {
        return@withContext fetch(uid, cachePolicy) {
            return@fetch gameDao.getFinishedGames(uid, null, limit)
        }
    }

    override suspend fun getNextHistory(
        uid: String, cachePolicy: CachePolicy, limit: Long
    ): Resource<List<Game>> = withContext(Dispatchers.IO) {
        return@withContext fetch(uid, cachePolicy) {
            val cached = cache[uid]?.value
            val new = gameDao.getFinishedGames(uid, cached?.lastOrNull()?.lastAction, limit)
            return@fetch if (cached != null) {
                if (new.data != null) {
                    Resource.success(cached.plus(new.data))
                } else {
                    Resource.success(cached)
                }
            } else {
                new
            }
        }
    }
}