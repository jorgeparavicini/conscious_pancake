package ch.ffhs.conscious_pancake.database.contracts

import android.net.Uri
import androidx.lifecycle.LiveData
import ch.ffhs.conscious_pancake.vo.Resource

interface IUserProfilePictureDao {
    fun loadImage(userId: String, imageName: String): LiveData<Resource<Uri>>

    fun uploadImage(userId: String, imageUri: Uri): LiveData<Resource<Unit>>
}