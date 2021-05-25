package ch.ffhs.conscious_pancake.repository

import androidx.lifecycle.LiveData
import ch.ffhs.conscious_pancake.database.GameDao
import ch.ffhs.conscious_pancake.repository.cache.CachePolicy
import ch.ffhs.conscious_pancake.repository.cache.CachePolicyRepository
import ch.ffhs.conscious_pancake.repository.contracts.IGameRepository
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.RemoteMove
import ch.ffhs.conscious_pancake.vo.Resource
import com.jorgeparavicini.draughts.model.core.Move
import com.jorgeparavicini.draughts.model.enums.Player
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val gameDao: GameDao
) : CachePolicyRepository<List<Game>>(), IGameRepository {

    override suspend fun getGames(
        uid: String, cachePolicy: CachePolicy, limit: Long
    ): Resource<List<Game>> = withContext(Dispatchers.IO) {
        return@withContext fetch(uid, cachePolicy) {
            return@fetch gameDao.getInProgressGames(uid, null, limit)
        }
    }

    override suspend fun getNextGames(
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

    fun getLiveGame(gameId: String): LiveData<Resource<Game?>> {
        return gameDao.getLiveGame(gameId)
    }

    suspend fun addMoveToGame(gameId: String, move: RemoteMove): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext gameDao.addMoveToGame(gameId, move)
        }

    suspend fun registerWinner(gameId: String, winner: Player): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext gameDao.registerWinner(gameId, winner)
        }
}