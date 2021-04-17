package ch.ffhs.conscious_pancake.repository.contracts

import ch.ffhs.conscious_pancake.utils.RefreshableLiveData
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.User

interface IUserRepository {
    fun getUser(userId: String): RefreshableLiveData<Resource<User>>

    suspend fun updateUser(userId: String, user: User): Resource<Unit>
}