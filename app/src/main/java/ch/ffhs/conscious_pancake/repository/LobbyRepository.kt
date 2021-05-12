package ch.ffhs.conscious_pancake.repository

import androidx.lifecycle.LiveData
import ch.ffhs.conscious_pancake.database.LobbyDao
import ch.ffhs.conscious_pancake.repository.cache.CachePolicy
import ch.ffhs.conscious_pancake.repository.cache.CachePolicyRepository
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Lobby
import ch.ffhs.conscious_pancake.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LobbyRepository @Inject constructor(private val lobbyDao: LobbyDao) : CachePolicyRepository<List<Lobby>>() {

    fun getLiveLobby(lobbyId: String): LiveData<Resource<Lobby?>> {
        return lobbyDao.getLiveLobby(lobbyId)
    }

    suspend fun getOpenLobbies(cachePolicy: CachePolicy, limit: Long): Resource<List<Lobby>> =
        withContext(Dispatchers.IO) {
            return@withContext fetch(OPEN_GAMES_CACHE_KEY, cachePolicy) {
                return@fetch lobbyDao.getOpenLobbies(null, limit)
            }
        }

    suspend fun getNextLobbies(cachePolicy: CachePolicy, limit: Long): Resource<List<Lobby>> =
        withContext(Dispatchers.IO) {
            return@withContext fetch(OPEN_GAMES_CACHE_KEY, cachePolicy) {
                val cached = cache[OPEN_GAMES_CACHE_KEY]?.value
                val new = lobbyDao.getOpenLobbies(cached?.lastOrNull()?.createdAt, limit)
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

    suspend fun createLobby(lobby: Lobby): Resource<Lobby> {
        return lobbyDao.createLobby(lobby)
    }

    suspend fun joinLobby(lobbyId: String, userId: String): Resource<Unit> {
        return lobbyDao.joinLobby(lobbyId, userId)
    }

    suspend fun leaveLobby(lobbyId: String, userId: String): Resource<Unit> {
        return lobbyDao.leaveLobby(lobbyId, userId)
    }

    suspend fun deleteLobby(lobbyId: String): Resource<Unit> {
        return lobbyDao.deleteLobby(lobbyId)
    }

    companion object {

        const val OPEN_GAMES_CACHE_KEY = "open_games"
    }
}