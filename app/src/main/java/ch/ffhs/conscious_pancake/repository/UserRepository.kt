package ch.ffhs.conscious_pancake.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ch.ffhs.conscious_pancake.database.UserDao
import ch.ffhs.conscious_pancake.database.UserProfilePictureDao
import ch.ffhs.conscious_pancake.model.Resource
import ch.ffhs.conscious_pancake.model.Status
import ch.ffhs.conscious_pancake.model.User
import ch.ffhs.conscious_pancake.repository.contracts.IUserRepository
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao, private val userProfilePictureDao: UserProfilePictureDao
) : IUserRepository {

    private val userCache = ConcurrentHashMap<String, LiveData<User>>()
    private val userProfilePictureCache = ConcurrentHashMap<String, LiveData<Resource<Uri>>>()

    override fun getUser(userId: String): LiveData<Resource<User>> {
        val result: LiveData<Resource<User>>
        if (!userCache.containsKey(userId)) {
            result = Transformations.switchMap(userDao.findByUid(userId)) {
                if (it.status == Status.LOADING) {
                    return@switchMap MutableLiveData(Resource.loading(it.data))
                }
                if (it.status == Status.ERROR) {
                    return@switchMap MutableLiveData(it)
                }

                val user = MutableLiveData(it)
                if (!userProfilePictureCache.containsKey(userId)) {
                    userProfilePictureCache[userId] =
                        userProfilePictureDao.loadImage(userId, it.data!!.profilePictureName)
                }
                val profilePicture = userProfilePictureCache[userId]!!


                val r = MediatorLiveData<Resource<User>>()
                r.addSource(user) {
                    r.value = combineUserAndProfilePictureData(user, profilePicture)
                }

                r.addSource(profilePicture) {
                    r.value = combineUserAndProfilePictureData(user, profilePicture)
                }

                return@switchMap r
            }

            userCache[userId] = Transformations.map(result) {
                return@map it.data
            }
        } else {
            result = Transformations.map(userCache[userId]!!) {
                return@map Resource.success(it)
            }
        }
        return result
    }

    override fun updateUser(
        userId: String, user: User, hasImageChanged: Boolean
    ): LiveData<Resource<User>> {
        val result = MediatorLiveData<Resource<User>>()
        var profilePictureUpdate = userProfilePictureCache[userId]
        if (hasImageChanged && user.profilePictureUri != null) {
            profilePictureUpdate = userProfilePictureDao.uploadImage(
                userId, user.profilePictureUri!!
            )
            userProfilePictureCache[userId] = Transformations.map(profilePictureUpdate) {
                // We do not want to store an error in the cache.
                return@map Resource.success(it.data)
            }
        }

        val userUpdate = userDao.updateUser(userId, user)
        userCache[userId] = Transformations.map(userUpdate) {
            return@map it.data
        }

        result.addSource(userUpdate) {
            result.value = combineUserAndProfilePictureData(
                userUpdate, profilePictureUpdate!!
            )
        }
        result.addSource(profilePictureUpdate!!) {
            result.value = combineUserAndProfilePictureData(
                userUpdate, profilePictureUpdate
            )
        }
        return result
    }

    private fun combineUserAndProfilePictureData(
        userResult: LiveData<Resource<User>>, profilePictureResult: LiveData<Resource<Uri>>
    ): Resource<User> {
        val user = userResult.value
        val profilePicture = profilePictureResult.value

        if (user?.status == Status.ERROR) {
            return Resource.error(user.message!!, user.data)
        }

        if (profilePicture?.status == Status.ERROR) {
            return Resource.error(profilePicture.message!!, user?.data)
        }

        if (user == null || profilePicture == null) {
            return Resource.loading(user?.data)
        }

        val userData = user.data
            ?: throw IllegalStateException("Fetched user profile, but no data was returned")
        val profilePictureData = profilePicture.data
            ?: throw IllegalStateException("Fetched profile picture, but no data was returned")

        val result = User().apply {
            username = userData.username
            profilePictureName = userData.profilePictureName
            profilePictureUri = profilePictureData
        }

        Timber.v("Fetched user data: $result")
        return Resource.success(result)
    }


}