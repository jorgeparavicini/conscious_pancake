package ch.ffhs.conscious_pancake.database.contracts

import androidx.lifecycle.LiveData
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.User

/**
 * User Firebase access Contract
 */
interface IUserDao {
    fun findByUid(uid: String): LiveData<Resource<User>>

    fun updateUser(uid: String, user: User): LiveData<Resource<Unit>>
}