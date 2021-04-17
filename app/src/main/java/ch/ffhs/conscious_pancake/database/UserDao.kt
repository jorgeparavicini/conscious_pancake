package ch.ffhs.conscious_pancake.database

import android.content.Context
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.database.contracts.IUserDao
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.vo.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Firebase Access
 */
class UserDao @Inject constructor(@ApplicationContext private val context: Context) : IUserDao {
    override suspend fun findByUid(uid: String): Resource<User> =
        suspendCancellableCoroutine { ctx ->
            val docRef = getUserDoc(uid)

            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    Timber.v("Fetched user data for user: $uid")
                    ctx.resume(Resource.success(document.toObject(User::class.java)!!))
                } else {
                    Timber.e("Could not fetch user document with id: $uid")
                    ctx.resume(Resource.error("Could not fetch user document.", null))
                }
            }.addOnFailureListener {
                Timber.e("Failed fetching user document. $it")
                ctx.resume(Resource.error("Could not fetch user document.", null))
            }
        }

    override suspend fun updateUser(uid: String, user: User): Resource<Unit> =
        suspendCancellableCoroutine { ctx ->
            val docRef = getUserDoc(uid)

            if (user.isDirty.value == true) {
                docRef.set(user).addOnSuccessListener {
                    Timber.i("Successfully updated user document to: $user")
                    ctx.resume(Resource.success(null))
                }.addOnFailureListener {
                    Timber.e("Failed to update user document. $it")
                    ctx.resume(Resource.error(context.getString(R.string.failed_user_update), null))
                }
            } else {
                ctx.resume(Resource.success(null))
            }
        }

    private fun getUserDoc(userId: String): DocumentReference {
        return Firebase.firestore.collection("users").document(userId)
    }
}