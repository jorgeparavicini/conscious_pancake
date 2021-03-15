package ch.ffhs.conscious_pancake.activities.main

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.activities.HistoryFragment
import ch.ffhs.conscious_pancake.activities.lobby.LobbyFragment
import ch.ffhs.conscious_pancake.activities.ProfileFragment

enum class MainScreen(
    @IdRes val menuItemId: Int,
    @StringRes val titleStringId: Int,
    val fragment: Fragment
) {
    LOBBY(R.id.action_lobby, R.string.lobby, LobbyFragment()),
    HISTORY(R.id.action_history, R.string.history, HistoryFragment()),
    PROFILE(R.id.action_profile, R.string.profile, ProfileFragment())
}