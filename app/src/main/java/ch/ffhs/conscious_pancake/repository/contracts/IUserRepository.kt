package ch.ffhs.conscious_pancake.repository.contracts

import ch.ffhs.conscious_pancake.repository.cache.CachePolicy
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.User

interface IUserRepository {
    suspend fun getUser(userId: String, cachePolicy: CachePolicy): Resource<User>

    suspend fun updateUser(userId: String, user: User): Resource<Unit>
}