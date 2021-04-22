package ch.ffhs.conscious_pancake.repository

import ch.ffhs.conscious_pancake.database.HistoryDao
import ch.ffhs.conscious_pancake.repository.contracts.IHistoryRepository
import ch.ffhs.conscious_pancake.utils.RefreshableFlowData
import ch.ffhs.conscious_pancake.vo.Game
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(private val historyDao: HistoryDao) : IHistoryRepository {

    private val historyCache = ConcurrentHashMap<String, RefreshableFlowData<Game>>()

    override fun getHistory(uid: String, limit: Long): RefreshableFlowData<Game> {
        if (!historyCache.containsKey(uid)) {
            val refreshable = RefreshableFlowData(limit) { start, count ->
                historyDao.getHistoryForUser(uid, start, count)
            }
            historyCache[uid] = refreshable
        }
        return historyCache[uid]!!
    }
}