package ch.ffhs.conscious_pancake.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.FragmentHomeBinding
import ch.ffhs.conscious_pancake.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber

class HomeFragment : Fragment(R.layout.fragment_home),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homePagerAdapter: HomePagerAdapter

    // View Lifecycle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        homePagerAdapter = HomePagerAdapter(arguments, requireActivity())

        homePagerAdapter.setItems(
            arrayListOf(
                MainScreen.LOBBY, MainScreen.HISTORY, MainScreen.PROFILE
            )
        )

        val defaultScreen = MainScreen.LOBBY
        scrollToScreen(defaultScreen)
        selectBottomNavigationViewMenuItem(defaultScreen.menuItemId)
        setTitle(defaultScreen.titleStringId)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
        return binding.apply {
            pager.adapter = homePagerAdapter
            pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val selectedScreen = homePagerAdapter.getItems()[position]
                    selectBottomNavigationViewMenuItem(selectedScreen.menuItemId)
                    setTitle(selectedScreen.titleStringId)
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

    // Navigation

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        getMainScreenForMenuItem(item.itemId)?.let {
            scrollToScreen(it)
            setTitle(it.titleStringId)
            return true
        }
        return false
    }

    private fun scrollToScreen(mainScreen: MainScreen) {
        val screenPosition = homePagerAdapter.getItems().indexOf(mainScreen)
        if (screenPosition != binding.pager.currentItem) {
            binding.pager.currentItem = screenPosition
        }
    }

    private fun selectBottomNavigationViewMenuItem(@IdRes menuItemId: Int) {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(null)
        binding.bottomNavigationView.selectedItemId = menuItemId
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    private fun getMainScreenForMenuItem(menuItemId: Int): MainScreen? {
        for (mainScreen in MainScreen.values()) {
            if (mainScreen.menuItemId == menuItemId) {
                return mainScreen
            }
        }
        return null
    }

    // Helper methods

    private fun setTitle(id: Int) {
        (activity as AppCompatActivity).supportActionBar?.setTitle(id)
    }

    private fun navigateToHostGame() {
        requireView().findNavController()
            .navigate(HomeFragmentDirections.actionHomeFragmentToHostGameFragment())
    }

    private fun navigateToJoinGame() {
        requireView().findNavController()
            .navigate(HomeFragmentDirections.actionHomeFragmentToJoinGameFragment())
    }
}