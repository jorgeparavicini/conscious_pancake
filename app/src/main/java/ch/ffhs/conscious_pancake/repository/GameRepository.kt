package ch.ffhs.conscious_pancake.repository

import ch.ffhs.conscious_pancake.database.GameDao
import ch.ffhs.conscious_pancake.repository.contracts.IGameRepository
import ch.ffhs.conscious_pancake.utils.RefreshableFlowData
import ch.ffhs.conscious_pancake.vo.Game
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(private val gameDao: GameDao) : IGameRepository {

    private val gameCache = ConcurrentHashMap<String, RefreshableFlowData<Game>>()

    override fun getGames(uid: String, limit: Long): RefreshableFlowData<Game> {
        if (!gameCache.containsKey(uid)) {
            val refreshable = RefreshableFlowData(limit) { count, size ->
                gameDao.getGamesForUser(uid, count, size)
            }

            gameCache[uid] = refreshable
        }

        return gameCache[uid]!!
    }
}