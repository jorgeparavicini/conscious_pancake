package ch.ffhs.conscious_pancake.repository.contracts

import androidx.lifecycle.LiveData
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.User

interface IUserRepository {
    fun getUser(userId: String): LiveData<Resource<User>>

    fun updateUser(
        userId: String,
        user: User,
    ): LiveData<Resource<Unit>>
}