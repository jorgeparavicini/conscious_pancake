package ch.ffhs.conscious_pancake.activities.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import ch.ffhs.conscious_pancake.BuildConfig
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.extensions.fileName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class ProfileFragment(private val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!) :
    Fragment(R.layout.activity_profile) {
    companion object {
        const val TAG = "Profile Fragment"
        const val PROFILE_PICTURE_STORAGE_PATH = "/profile_pictures/"
    }

    private val remoteImage: Uri?
        get() = user.photoUrl

    private var imageUri: Uri? = null
    private var previousImage: Uri? = null
    private val cameraActivityLauncher = createCameraActivityLauncher { onImageTaken() }
    var isDirty = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, FirebaseAuth.getInstance().currentUser!!.photoUrl.toString())

        view.findViewById<TextView>(R.id.profile_username).text = user.displayName
        view.findViewById<TextView>(R.id.profile_email).text = user.email
        view.findViewById<TextView>(R.id.profile_id).text = user.uid
        getProfilePicture { view.findViewById<ImageView>(R.id.profile_image).setImageURI(it) }

        // Setup event handlers
        view.findViewById<Button>(R.id.profile_change_image).setOnClickListener { takePicture() }
        view.findViewById<Button>(R.id.update_profile_button).setOnClickListener { saveChanges() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.profile_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Creates the [ActivityResultLauncher] for taking the profile picture.
     * When launched, it will start the camera activity and call the [imageTaken] callback if it succeeded.
     */
    private fun createCameraActivityLauncher(imageTaken: () -> Unit): ActivityResultLauncher<Uri> {
        return registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                imageTaken()
            } else {
                Toast.makeText(requireContext(), R.string.unexpected_error, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * Starts the activity to take a picture which will be used as a profile picture.
     */
    private fun takePicture() {
        previousImage = imageUri
        val path = createTemporaryProfilePictureFile()
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            BuildConfig.APPLICATION_ID + ".fileprovider", File(path.toString())
        )
        cameraActivityLauncher.launch(imageUri)
    }

    /**
     * Creates an image with a generated uuid and returns to path to that created image.
     */
    private fun createTemporaryProfilePictureFile(): Path {
        val uid = UUID.randomUUID().toString()
        Log.d(TAG, "Creating temporary file: $uid.jpg")
        return Files.createTempFile(uid, ".jpg")
    }

    /**
     * Called when the image was taken and stored in the imageUri variable.
     *
     * Updates the profile image view
     * Marks this profile as dirty
     * Removes any unused images
     */
    private fun onImageTaken() {
        requireView().findViewById<ImageView>(R.id.profile_image).setImageURI(imageUri)
        isDirty = true

        // The user can take multiple pictures, but we only need 2 references.
        // His currently saved profile picture aka remoteImage and his last taken picture.
        // Since he just took another picture, the previousImage is the second last picture
        // which we don't need anymore, hence we delete it if it exists.
        if (previousImage?.fileName != null && previousImage?.fileName != remoteImage?.fileName) {
            deleteLocalProfilePicture(previousImage!!)
        }
    }

    /**
     * Downloads the profile picture if required and returns it in the [displayProfilePicture] callback
     */
    private fun getProfilePicture(displayProfilePicture: (uri: Uri) -> Unit) {
        user.photoUrl ?: return

        imageUri = File(requireContext().cacheDir, user.photoUrl!!.fileName).toUri()

        if (Files.exists(Paths.get(imageUri!!.path))) {
            Log.d(TAG, "User profile picture already downloaded")
            displayProfilePicture(imageUri!!)
        } else {
            Log.d(TAG, "Downloading profile picture")
            val storageRef = getStorageReferenceFor(user.photoUrl!!)

            storageRef.getFile(imageUri!!).addOnSuccessListener {
                Log.d(TAG, "Successfully downloaded profile picture ${imageUri?.fileName}")
                displayProfilePicture(imageUri!!)
            }.addOnFailureListener {
                Log.e(TAG, "Failed to download profile picture ${user.photoUrl}")
            }
        }
    }

    /**
     * Uploads the image located at [uri] to the [user]s firebase storage folder.
     * The image is stored under the folder [PROFILE_PICTURE_STORAGE_PATH]
     * and has the same name as the file located at [uri]
     *
     * [complete] is called when the upload completed successfully.
     * If there was an error uploading the image, [error] is called
     */
    private fun uploadProfilePicture(
        uri: Uri,
        complete: () -> Unit = {},
        error: () -> Unit = {}
    ) {
        val filename = uri.fileName
        val storageRef =
            Firebase.storage.getReference(user.uid + PROFILE_PICTURE_STORAGE_PATH + filename)
        storageRef.putFile(uri).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "Uploaded new profile picture to $filename")
                complete()
            } else {
                Log.e(TAG, "Failed to upload profile picture to $filename")
                error()
            }
        }
    }

    /**
     * Deletes the existing profile picture and uploads the new one.
     */
    private fun updateRemoteProfilePicture(
        uri: Uri,
        complete: () -> Unit = {},
        error: () -> Unit = {}
    ) {
        if (user.photoUrl != null && user.photoUrl?.fileName != uri.fileName) {
            deleteRemoteProfilePicture(user.photoUrl!!)
        }
        return uploadProfilePicture(uri, complete, error)
    }

    /**
     * Deletes an existing profile picture referenced by [uri]
     */
    private fun deleteRemoteProfilePicture(uri: Uri) {
        Log.d(TAG, "Removing remote profile picture ${uri.fileName}")
        val storageRef = getStorageReferenceFor(uri)
        storageRef.delete()
    }

    private fun saveChanges() {
        if (imageUri != null) {
            if (remoteImage?.fileName != null && remoteImage?.fileName != imageUri?.fileName)
                deleteLocalProfilePicture(remoteImage!!)
            updateRemoteProfilePicture(imageUri!!)
        }

        val updateRequest = userProfileChangeRequest {
            displayName = user.displayName
            photoUri = if (imageUri != null) Uri.parse(imageUri!!.fileName) else null
        }
        user.updateProfile(updateRequest).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Successfully updated profile")
            }
        }
    }

    private fun deleteLocalProfilePicture(uri: Uri) {
        Log.d(TAG, "Removing local image ${uri.fileName}")
        val path = Paths.get(requireContext().cacheDir.toString() + "/" + uri.fileName)
        Files.deleteIfExists(path)
    }


    private fun getStorageReferenceFor(uri: Uri): StorageReference {
        return Firebase.storage.getReference(user.uid + PROFILE_PICTURE_STORAGE_PATH + uri.fileName)
    }
}