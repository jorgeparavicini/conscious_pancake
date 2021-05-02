package ch.ffhs.conscious_pancake.repository

import androidx.lifecycle.LiveData
import ch.ffhs.conscious_pancake.database.LobbyDao
import ch.ffhs.conscious_pancake.repository.cache.CachePolicyRepository
import ch.ffhs.conscious_pancake.vo.Lobby
import ch.ffhs.conscious_pancake.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LobbyRepository @Inject constructor(private val lobbyDao: LobbyDao) : CachePolicyRepository<List<Lobby>>() {

    fun getLiveLobby(lobbyId: String): LiveData<Resource<Lobby?>> {
        return lobbyDao.getLiveLobby(lobbyId)
    }

    suspend fun createLobby(lobby: Lobby): Resource<Lobby> {
        return lobbyDao.createLobby(lobby)
    }

    suspend fun deleteLobby(lobbyId: String): Resource<Unit> {
        return lobbyDao.deleteLobby(lobbyId)
    }
}