package ch.ffhs.conscious_pancake.database.contracts

import android.net.Uri
import ch.ffhs.conscious_pancake.vo.Resource

interface IUserProfilePictureDao {
    suspend fun loadImage(userId: String, imageName: String): Resource<Uri>

    suspend fun uploadImage(userId: String, imageUri: Uri): Resource<Unit>
}