package ch.ffhs.conscious_pancake.ui.home

import android.os.Bundle
import android.view.*
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class HomeFragment : Fragment(R.layout.fragment_home),
                     BottomNavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homePagerAdapter: HomePagerAdapter

    // View Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        homePagerAdapter = HomePagerAdapter(arguments, requireActivity())

        homePagerAdapter.setItems(
            arrayListOf(
                HomeScreen.GAMES, HomeScreen.HISTORY, HomeScreen.PROFILE
            )
        )

        val defaultScreen = HomeScreen.GAMES
        goToScreen(defaultScreen)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
        return binding.apply {
            pager.adapter = homePagerAdapter
            pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val selectedScreen = homePagerAdapter.getItems()[position]
                    goToScreen(selectedScreen)
                }
            })

            fabHostGame.setOnClickListener { navigateToHostGame() }
            fabJoinGame.setOnClickListener { navigateToJoinGame() }
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.logout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> logout()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout(): Boolean {
        Firebase.auth.signOut()
        requireView().findNavController().popBackStack(R.id.titleFragment, false)
        return true
    }

    // Navigation

    private fun goToScreen(screen: HomeScreen) {
        Timber.v("Selected screen ${requireContext().getString(screen.titleStringId)}")
        if (binding.bottomNavigationView.selectedItemId != screen.menuItemId) {
            selectBottomNavigationViewMenuItem(screen.menuItemId)
        }
        scrollToScreen(screen)
        setTitle(screen.titleStringId)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        getScreenForId(item.itemId)?.let {
            goToScreen(it)
            return true
        }
        return false
    }

    private fun scrollToScreen(homeScreen: HomeScreen) {
        val screenPosition = homePagerAdapter.getItems().indexOf(homeScreen)
        if (screenPosition != binding.pager.currentItem) {
            binding.pager.currentItem = screenPosition
        }
    }

    private fun selectBottomNavigationViewMenuItem(@IdRes menuItemId: Int) {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(null)
        binding.bottomNavigationView.selectedItemId = menuItemId
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    private fun getScreenForId(menuItemId: Int): HomeScreen? {
        for (mainScreen in HomeScreen.values()) {
            if (mainScreen.menuItemId == menuItemId) {
                return mainScreen
            }
        }
        return null
    }

    private fun setTitle(id: Int) {
        (activity as AppCompatActivity).supportActionBar?.setTitle(id)
    }

    // Navigation

    private fun navigateToHostGame() {
        requireView().findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToHostGameFragment())
    }

    private fun navigateToJoinGame() {
        requireView().findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToJoinGameFragment())
    }
}