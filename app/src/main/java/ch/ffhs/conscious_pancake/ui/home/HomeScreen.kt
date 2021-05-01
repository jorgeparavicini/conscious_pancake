package ch.ffhs.conscious_pancake.ui.home

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.ui.history.HistoryFragment
import ch.ffhs.conscious_pancake.ui.games.GamesFragment
import ch.ffhs.conscious_pancake.ui.profile.ProfileFragment

enum class HomeScreen(
    @IdRes val menuItemId: Int,
    @StringRes val titleStringId: Int
) {
    GAMES(R.id.action_games, R.string.games) {

        override val fragment: Fragment
            get() = GamesFragment()
    },
    HISTORY(R.id.action_history, R.string.history) {

        override val fragment: Fragment
            get() = HistoryFragment()
    },
    PROFILE(R.id.action_profile, R.string.profile) {

        override val fragment: Fragment
            get() = ProfileFragment()
    };

    abstract val fragment: Fragment
}