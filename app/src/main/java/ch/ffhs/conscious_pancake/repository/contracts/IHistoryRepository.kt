package ch.ffhs.conscious_pancake.repository.contracts

import ch.ffhs.conscious_pancake.utils.RefreshableFlowData
import ch.ffhs.conscious_pancake.vo.Game

interface IHistoryRepository {
    fun getHistory(uid: String, limit: Long = 10): RefreshableFlowData<Game>
}