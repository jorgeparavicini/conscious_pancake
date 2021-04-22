package ch.ffhs.conscious_pancake.repository

import ch.ffhs.conscious_pancake.database.LobbyDao
import ch.ffhs.conscious_pancake.database.UserDao
import ch.ffhs.conscious_pancake.repository.contracts.ILobbyRepository
import ch.ffhs.conscious_pancake.utils.RefreshableFlowData
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.Status
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LobbyRepository @Inject constructor(
    private val lobbyDao: LobbyDao, private val userDao: UserDao
) : ILobbyRepository {

    private val lobbyCache = ConcurrentHashMap<String, RefreshableFlowData<Game>>()

    override fun getLobbies(uid: String, limit: Long): RefreshableFlowData<Game> {
        if (!lobbyCache.containsKey(uid)) {
            val refreshable = RefreshableFlowData<Game>(limit) { start, count ->
                return@RefreshableFlowData coroutineScope {
                    var lobbies = lobbyDao.getLobbiesForUser(uid, start, count)
                    if (lobbies.status == Status.SUCCESS) {
                         val l = lobbies.data!!.map { game ->
                            return@map async {
                                game.player1 = userDao.findByUid(game.player1Id).data
                                game.player2 = userDao.findByUid(game.player2Id).data
                                return@async game
                            }
                        }.awaitAll()
                        lobbies = Resource.success(l)
                    }
                    return@coroutineScope lobbies
                }
            }

            lobbyCache[uid] = refreshable
        }

        return lobbyCache[uid]!!
    }
}