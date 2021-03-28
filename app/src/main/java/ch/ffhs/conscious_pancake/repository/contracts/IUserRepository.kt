package ch.ffhs.conscious_pancake.repository.contracts

import androidx.lifecycle.LiveData
import ch.ffhs.conscious_pancake.model.Resource
import ch.ffhs.conscious_pancake.model.User

interface IUserRepository {
    fun getUser(userId: String): LiveData<Resource<User>>

    fun updateUser(
        userId: String,
        user: User,
        hasImageChanged: Boolean = false
    ): LiveData<Resource<User>>
}