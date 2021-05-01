package ch.ffhs.conscious_pancake.database.contracts

import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.Resource
import com.google.firebase.Timestamp

interface IGameDao {

    suspend fun getFinishedGames(
        userId: String, lastDocKey: Timestamp?, count: Long
    ): Resource<List<Game>>

    suspend fun getInProgressGames(
        userId: String, lastDocKey: Timestamp?, count: Long
    ): Resource<List<Game>>
}