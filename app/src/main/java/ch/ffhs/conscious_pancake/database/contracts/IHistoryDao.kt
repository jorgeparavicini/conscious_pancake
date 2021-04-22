package ch.ffhs.conscious_pancake.database.contracts

import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource

interface IHistoryDao {
    suspend fun getHistoryForUser(
        uid: String, start: Int, count: Long = 10
    ): Resource<List<Game>>
}