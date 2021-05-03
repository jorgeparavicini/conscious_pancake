package ch.ffhs.conscious_pancake.database.contracts

import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.User

/**
 * User Firebase access Contract
 */
interface IUserDao {
    suspend fun findByUid(uid: String): Resource<User>

    suspend fun updateUser(uid: String, user: User): Resource<Unit>
}