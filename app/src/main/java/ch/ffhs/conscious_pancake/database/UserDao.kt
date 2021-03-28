package ch.ffhs.conscious_pancake.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.database.contracts.IUserDao
import ch.ffhs.conscious_pancake.model.Resource
import ch.ffhs.conscious_pancake.model.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Firebase Access
 */
class UserDao @Inject constructor(@ApplicationContext private val context: Context): IUserDao {
    override fun findByUid(uid: String): LiveData<Resource<User>> {
        val result = MutableLiveData<Resource<User>>()
        val docRef = getUserDoc(uid)

        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                result.value = Resource.success(document.toObject(User::class.java))
                // Consume the is dirty flag
                result.value?.data?.didSave()
                Timber.i("Not Dirty")
            } else {
                Timber.e("Could not fetch user document with id: $uid")
                result.value = Resource.error("Could not fetch user document.", null)
            }
        }.addOnFailureListener {
            Timber.e("Failed fetching user document. $it")
            result.value = Resource.error("Could not fetch user document.", null)
        }
        return result
    }

    override fun updateUser(uid: String, user: User): LiveData<Resource<User>> {
        val result = MutableLiveData<Resource<User>>()
        val docRef = getUserDoc(uid)

        docRef.set(user).addOnSuccessListener {
            Timber.i("Successfully updated user document to: $user")
            result.value = Resource.success(user)
        } .addOnFailureListener {
            Timber.e("Failed to update user document. $it")
            result.value = Resource.error(context.getString(R.string.failed_user_update), user)
        }

        return result
    }

    private fun getUserDoc(userId: String): DocumentReference {
        return Firebase.firestore.collection("users").document(userId)
    }
}