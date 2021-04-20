package ch.ffhs.conscious_pancake.ui.title

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.FragmentTitleBinding
import ch.ffhs.conscious_pancake.ui.auth.AuthResultContract
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber


class TitleFragment : Fragment(R.layout.fragment_title) {

    private val authResultLauncher =
        registerForActivityResult(AuthResultContract()) { idpResponse ->
            handleAuthResponse(idpResponse)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding: FragmentTitleBinding = FragmentTitleBinding.inflate(inflater, container, false)

        return binding.apply {
            buttonLogin.setOnClickListener { onLogin() }
        }.root
    }

    private fun onLogin() {
        val user = Firebase.auth.currentUser
        Timber.d("Dispatching user: ${user?.uid}")
        if (user != null) {
            navigateToHome()
        } else {
            authResultLauncher.launch(RC_SIGN_IN)
        }
    }

    private fun handleAuthResponse(idpResponse: IdpResponse?) {
        when {
            (idpResponse == null || idpResponse.error != null) -> {
                Timber.i("Login cancelled")
            }
            else -> {
                navigateToHome()
            }
        }
    }

    private fun navigateToHome() {
        val navController = requireView().findNavController()
        val uid = Firebase.auth.currentUser!!.uid
        Timber.i("Going to home fragment with user $uid")
        navController.navigate(TitleFragmentDirections.actionTitleFragmentToHomeFragment(uid))
    }

    companion object {
        const val RC_SIGN_IN = 8031
    }
}