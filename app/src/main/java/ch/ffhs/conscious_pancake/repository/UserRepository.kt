package ch.ffhs.conscious_pancake.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ch.ffhs.conscious_pancake.database.UserDao
import ch.ffhs.conscious_pancake.database.UserProfilePictureDao
import ch.ffhs.conscious_pancake.repository.contracts.IUserRepository
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.Status
import ch.ffhs.conscious_pancake.vo.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao, private val userProfilePictureDao: UserProfilePictureDao
) : IUserRepository {
    override fun getUser(userId: String): LiveData<Resource<User>> {
        return Transformations.switchMap(userDao.findByUid(userId)) { user ->
            // If it is loading or an error occurred we can not get the image.
            if (user.status in listOf(Status.ERROR, Status.LOADING)) {
                return@switchMap MutableLiveData(user)
            }

            Transformations.switchMap(
                userProfilePictureDao.loadImage(
                    userId, user.data!!.profilePictureName
                )
            ) { image ->
                if (image.status == Status.SUCCESS) {
                    user.data.profilePictureUri = image.data
                }

                MutableLiveData(user)
            }
        }
    }

    override fun updateUser(
        userId: String, user: User
    ): LiveData<Resource<Unit>> {
        val userUpdate = userDao.updateUser(userId, user)

        val result = if (user.imageChanged && user.profilePictureUri != null) {
            val pictureUpdate = userProfilePictureDao.uploadImage(userId, user.profilePictureUri!!)
            val result = MediatorLiveData<Resource<Unit>>()
            result.addSource(userUpdate) {
                result.value = combineResults(userUpdate, pictureUpdate)
            }
            result.addSource(pictureUpdate) {
                result.value = combineResults(userUpdate, pictureUpdate)
            }
            result
        } else {
            userUpdate
        }

        user.reset()
        return result
    }

    // TODO: Make generic and move out of here
    private fun combineResults(
        firstResult: LiveData<Resource<Unit>>, secondResult: LiveData<Resource<Unit>>
    ): Resource<Unit> {
        val first = firstResult.value
        val second = secondResult.value

        if (first?.status == Status.ERROR) {
            return Resource.error(first.message!!, null)
        }

        if (second?.status == Status.ERROR) {
            return Resource.error(second.message!!, null)
        }

        if (first == null || second == null || first.status == Status.LOADING || second.status == Status.LOADING) {
            return Resource.loading(null)
        }

        return Resource.success(null)
    }
}