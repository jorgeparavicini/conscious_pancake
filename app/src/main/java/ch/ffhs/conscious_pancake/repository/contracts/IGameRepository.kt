package ch.ffhs.conscious_pancake.repository.contracts

import ch.ffhs.conscious_pancake.utils.RefreshableFlowData
import ch.ffhs.conscious_pancake.vo.Game

interface IGameRepository {
    fun getGames(uid: String, limit: Long = 10): RefreshableFlowData<Game>
}