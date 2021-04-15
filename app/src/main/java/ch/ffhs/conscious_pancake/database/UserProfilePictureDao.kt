package ch.ffhs.conscious_pancake.database

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.database.contracts.IUserProfilePictureDao
import ch.ffhs.conscious_pancake.vo.Resource
import ch.ffhs.conscious_pancake.utils.fileName
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject


class UserProfilePictureDao @Inject constructor(@ApplicationContext private val context: Context) :
    IUserProfilePictureDao {

    companion object {
        const val RemoteSavingPath = "/images/profile_picture.jpg"
    }

    override fun loadImage(userId: String, imageName: String): LiveData<Resource<Uri>> {
        val result = MutableLiveData<Resource<Uri>>()
        val localImage = File(context.cacheDir, imageName).toUri()

        if (Files.exists(Paths.get(localImage.path))) {
            Timber.v("Getting cached image for user $userId. Image name: $imageName")
            result.value = Resource.success(localImage)
        } else {
            Timber.v("Downloading profile picture for user: $userId. Image name: $imageName")
            val storageRef = Firebase.storage.getReference(userId + RemoteSavingPath)
            storageRef.getFile(localImage).addOnSuccessListener {
                Timber.d("Downloaded profile picture for user: $userId. Image name: $imageName")
                result.value = Resource.success(localImage)
            }.addOnFailureListener {
                Timber.e("Failed to download profile picture for user: $userId. Image name: $imageName. Exception: $it")
                result.value =
                    Resource.error(context.getString(R.string.profile_picture_download_failed),
                        null
                    )
            }
        }

        return result
    }

    override fun uploadImage(userId: String, imageUri: Uri): LiveData<Resource<Unit>> {
        Timber.v("Uploading profile picture for user: $userId. Image: ${imageUri.fileName}")
        val result = MutableLiveData<Resource<Unit>>()
        val storageRef =
            Firebase.storage.getReference(userId + RemoteSavingPath)
        storageRef.putFile(imageUri).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.d("Successfully uploaded profile picture for user: $userId. Image: ${imageUri.fileName}")
                result.value = Resource.success(null)
            } else {
                Timber.e("Failed to upload profile picture for user: $userId. Image name: ${imageUri.fileName}. Exception: ${it.exception}")
                result.value =
                    Resource.error(context.getString(R.string.profile_picture_upload_failed), null)
            }
        }.addOnFailureListener {
            Timber.e("Failed to upload profile picture for user: $userId. Image name: ${imageUri.fileName}. Exception: $it")
            result.value =
                Resource.error(context.getString(R.string.profile_picture_upload_failed), null)
        }

        return result
    }
}