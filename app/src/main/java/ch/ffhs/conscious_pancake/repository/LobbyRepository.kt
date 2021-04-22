package ch.ffhs.conscious_pancake.repository

import ch.ffhs.conscious_pancake.database.LobbyDao
import ch.ffhs.conscious_pancake.repository.contracts.ILobbyRepository
import ch.ffhs.conscious_pancake.utils.RefreshableFlowData
import ch.ffhs.conscious_pancake.vo.Game
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LobbyRepository @Inject constructor(private val lobbyDao: LobbyDao) : ILobbyRepository {

    private val lobbyCache = ConcurrentHashMap<String, RefreshableFlowData<Game>>()

    override fun getLobbies(uid: String, limit: Long): RefreshableFlowData<Game> {
        if (!lobbyCache.containsKey(uid)) {
            val refreshable = RefreshableFlowData(limit) { count, size ->
                lobbyDao.getLobbiesForUser(uid, count, size)
            }

            lobbyCache[uid] = refreshable
        }

        return lobbyCache[uid]!!
    }
}