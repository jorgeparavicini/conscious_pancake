package ch.ffhs.conscious_pancake.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ch.ffhs.conscious_pancake.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse

class AuthResultContract : ActivityResultContract<Int, IdpResponse>() {
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
    )

    override fun createIntent(context: Context, input: Int?): Intent =
        AuthUI.getInstance().createSignInIntentBuilder()
            .setLogo(R.drawable.common_google_signin_btn_icon_dark)
            .setTheme(R.style.Theme_ConsciousPancake_Auth).setAvailableProviders(providers)
            .setIsSmartLockEnabled(false).build().apply { putExtra(INPUT_INT, input) }

    override fun parseResult(resultCode: Int, intent: Intent?): IdpResponse? = when (resultCode) {
        Activity.RESULT_OK -> IdpResponse.fromResultIntent(intent)
        else -> null
    }

    companion object {
        const val INPUT_INT = "input_int"
    }
}