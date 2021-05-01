package ch.ffhs.conscious_pancake.repository

import ch.ffhs.conscious_pancake.database.GameDao
import ch.ffhs.conscious_pancake.repository.cache.CachePolicy
import ch.ffhs.conscious_pancake.repository.cache.CachePolicyRepository
import ch.ffhs.conscious_pancake.repository.contracts.ILobbyRepository
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LobbyRepository @Inject constructor(
    private val gameDao: GameDao
) : CachePolicyRepository<List<Game>>(), ILobbyRepository {

    override suspend fun getLobbies(
        uid: String, cachePolicy: CachePolicy, limit: Long
    ): Resource<List<Game>> = withContext(Dispatchers.IO) {
        return@withContext fetch(uid, cachePolicy) {
            return@fetch gameDao.getInProgressGames(uid, null, limit)
        }
    }

    override suspend fun getNextLobbies(
        uid: String, cachePolicy: CachePolicy, limit: Long
    ): Resource<List<Game>> = withContext(Dispatchers.IO) {
        return@withContext fetch(uid, cachePolicy) {
            val cached = cache[uid]?.value
            val new =
                gameDao.getInProgressGames(uid, cache[uid]?.value?.lastOrNull()?.lastAction, limit)
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