package ch.ffhs.conscious_pancake.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ch.ffhs.conscious_pancake.BuildConfig
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private var imageUri: Uri? = null

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            viewModel.setProfilePicture(imageUri!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        return binding.apply {
            profileViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
            profileChangeImage.setOnClickListener {
                takePicture()
            }
            updateProfileButton.setOnClickListener { viewModel.saveChanges() }
        }.root
    }


    /* Options Menu */

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        if (viewModel.isEditable) {
            inflater.inflate(R.menu.profile_fragment_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                viewModel.toggleEditing()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    /**
     * Starts the activity to take a picture which will be used as a profile picture.
     */
    private fun takePicture() {
        val path = createTemporaryProfilePictureFile()
        imageUri = FileProvider.getUriForFile(
            requireContext(), BuildConfig.APPLICATION_ID + ".fileprovider", File(path.toString())
        )
        cameraLauncher.launch(imageUri)
    }

    /**
     * Creates an image with a generated uuid and returns to path to that created image.
     */
    private fun createTemporaryProfilePictureFile(): Path {
        val uid = UUID.randomUUID().toString()
        return Files.createTempFile(uid, ".jpg")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}