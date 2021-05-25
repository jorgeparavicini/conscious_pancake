package ch.ffhs.conscious_pancake.database

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.database.contracts.IUserProfilePictureDao
import ch.ffhs.conscious_pancake.utils.fileName
import ch.ffhs.conscious_pancake.vo.Resource
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject
import kotlin.coroutines.resume


class UserProfilePictureDao @Inject constructor(@ApplicationContext private val context: Context) :
    IUserProfilePictureDao {

    companion object {
        const val RemoteSavingPath = "/images/profile_picture.jpg"
    }

    override suspend fun loadImage(userId: String, imageName: String): Resource<Uri> =
        suspendCancellableCoroutine { ctx ->
            val localImage = File(context.cacheDir, imageName).toUri()

            if (Files.exists(Paths.get(localImage.path))) {
                Timber.v("Getting cached image for user $userId. Image name: $imageName")
                ctx.resume(Resource.success(localImage))
            } else {
                Timber.v("Downloading profile picture for user: $userId. Image name: $imageName")
                val storageRef = Firebase.storage.getReference(userId + RemoteSavingPath)
                storageRef.getFile(localImage).addOnSuccessListener {
                    Timber.d("Downloaded profile picture for user: $userId. Image name: $imageName")
                    ctx.resume(Resource.success(localImage))
                }.addOnFailureListener {
                    Timber.e("Failed to download profile picture for user: $userId. Image name: $imageName. Exception: $it")
                    ctx.resume(
                        Resource.error(
                            context.getString(R.string.profile_picture_download_failed), null
                        )
                    )
                }
            }
        }

    override suspend fun uploadImage(userId: String, imageUri: Uri): Resource<Unit> =
        suspendCancellableCoroutine { ctx ->
            Timber.v("Uploading profile picture for user: $userId. Image: ${imageUri.fileName}")

            val storageRef = Firebase.storage.getReference(userId + RemoteSavingPath)
            storageRef.putFile(imageUri).addOnCompleteListener {
                if (it.isSuccessful) {
                    Timber.d("Successfully uploaded profile picture for user: $userId. Image: ${imageUri.fileName}")
                    ctx.resume(Resource.success(null))
                } else {
                    Timber.e("Failed to upload profile picture for user: $userId. Image name: ${imageUri.fileName}. Exception: ${it.exception}")
                    ctx.resume(
                        Resource.error(
                            context.getString(R.string.profile_picture_upload_failed), null
                        )
                    )
                }
            }.addOnFailureListener {
                Timber.e("Failed to upload profile picture for user: $userId. Image name: ${imageUri.fileName}. Exception: $it")
                ctx.resume(
                    Resource.error(context.getString(R.string.profile_picture_upload_failed), null)
                )
            }

        }
}