package ch.ffhs.conscious_pancake.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ch.ffhs.conscious_pancake.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        AuthUI.getInstance().signOut(this)
        createSignInIntent()
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build(), RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser;
            }
        }
    }

    fun signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            Log.d("TAG", "hm")
        }
    }

    fun delete() {
        AuthUI.getInstance().delete(this).addOnCompleteListener {}
    }

    fun themeAndLogo() {
        val providers = emptyList<AuthUI.IdpConfig>()

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .setLogo(R.drawable.common_full_open_on_phone)
                .setTheme(R.style.ThemeOverlay_AppCompat_Dark).build(), RC_SIGN_IN
        )
    }

    fun privacyAndTerms() {
        val providers = emptyList<AuthUI.IdpConfig>()

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .setTosAndPrivacyPolicyUrls("", "").build(), RC_SIGN_IN
        )
    }

    companion object {
        private const val RC_SIGN_IN = 7421
    }
}