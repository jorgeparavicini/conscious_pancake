package ch.ffhs.conscious_pancake.database.contracts

import androidx.lifecycle.LiveData
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.User
import com.google.firebase.inject.Deferred
import kotlinx.coroutines.Job

/**
 * User Firebase access Contract
 */
interface IUserDao {
    suspend fun findByUid(uid: String): Resource<User>

    suspend fun updateUser(uid: String, user: User): Resource<Unit>
}