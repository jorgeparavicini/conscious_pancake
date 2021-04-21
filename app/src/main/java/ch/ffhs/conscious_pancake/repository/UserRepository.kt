package ch.ffhs.conscious_pancake.repository

import ch.ffhs.conscious_pancake.database.UserDao
import ch.ffhs.conscious_pancake.database.UserProfilePictureDao
import ch.ffhs.conscious_pancake.repository.contracts.IUserRepository
import ch.ffhs.conscious_pancake.utils.RefreshableLiveData
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.Status
import ch.ffhs.conscious_pancake.vo.User
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao, private val userProfilePictureDao: UserProfilePictureDao
) : IUserRepository {

    private val userCache = ConcurrentHashMap<String, RefreshableLiveData<Resource<User>>>()

    override fun getUser(
        userId: String
    ): RefreshableLiveData<Resource<User>> {
        if (!userCache.containsKey(userId)) {
            val refreshable = RefreshableLiveData {
                val userResult = userDao.findByUid(userId)
                if (userResult.status == Status.SUCCESS && userResult.data?.imageUUID != null) {
                    val profilePictureResult = userProfilePictureDao.loadImage(
                        userId, userResult.data.imageUUID!!
                    )
                    if (profilePictureResult.status == Status.SUCCESS) {
                        userResult.data.profilePictureUri = profilePictureResult.data
                    }
                }
                userResult.data?.startTracking()
                userResult
            }
            userCache[userId] = refreshable
        }
        return userCache[userId]!!
    }

    override suspend fun updateUser(
        userId: String, user: User
    ): Resource<Unit> = withContext(Dispatchers.IO) {
        val userUpdate = withContext(Dispatchers.Default) {
            userDao.updateUser(userId, user)
        }
        val pictureUpdate = if (user.imageChanged && user.profilePictureUri != null) withContext(
            Dispatchers.Default
        ) {
            userProfilePictureDao.uploadImage(userId, user.profilePictureUri!!)
        } else null

        // Consume dirty flag to hide update button
        // Must be done on UI Thread as it modifies a live data object
        withContext(Dispatchers.Main) {
            user.reset()
        }

        if (userUpdate.status == Status.ERROR) {
            return@withContext Resource.error(userUpdate.message!!)
        }
        if (pictureUpdate?.status == Status.ERROR) {
            return@withContext Resource.error(pictureUpdate.message!!)
        }

        return@withContext Resource.success()
    }
}