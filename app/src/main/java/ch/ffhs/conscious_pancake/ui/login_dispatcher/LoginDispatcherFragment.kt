package ch.ffhs.conscious_pancake.ui.login_dispatcher

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ch.ffhs.conscious_pancake.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

/**
 * TODO: Show a loading wheel
 */
class LoginDispatcherFragment : Fragment(R.layout.fragment_login_dispatcher) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dispatch()
    }

    private fun dispatch() {
        val user = Firebase.auth.currentUser
        Timber.d("Dispatching user: ${user?.uid}")
        if (user != null) {
            requireView().findNavController()
                .navigate(LoginDispatcherFragmentDirections.actionLoginDispatcherToHomeFragment(user.uid))
        } else {
            requireView().findNavController()
                .navigate(LoginDispatcherFragmentDirections.actionLoginDispatcherToLoginActivity())
        }
    }
}