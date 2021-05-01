package ch.ffhs.conscious_pancake.repository

import ch.ffhs.conscious_pancake.database.LobbyDao
import ch.ffhs.conscious_pancake.repository.cache.CachePolicyRepository
import ch.ffhs.conscious_pancake.vo.Lobby
import ch.ffhs.conscious_pancake.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LobbyRepository @Inject constructor(private val lobbyDao: LobbyDao) : CachePolicyRepository<List<Lobby>>() {

    suspend fun createLobby(lobby: Lobby): Resource<Lobby> {
        return lobbyDao.createLobby(lobby)
    }
}