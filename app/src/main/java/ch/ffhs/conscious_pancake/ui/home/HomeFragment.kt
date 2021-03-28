package ch.ffhs.conscious_pancake.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.FragmentHomeBinding
import ch.ffhs.conscious_pancake.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber

class HomeFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homePagerAdapter: HomePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(arguments?.getString("user_id"))
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homePagerAdapter = HomePagerAdapter(arguments, requireActivity())

        homePagerAdapter.setItems(
            arrayListOf(
                MainScreen.LOBBY,
                MainScreen.HISTORY,
                MainScreen.PROFILE
            )
        )

        val defaultScreen = MainScreen.LOBBY
        scrollToScreen(defaultScreen)
        selectBottomNavigationViewMenuItem(defaultScreen.menuItemId)
        setTitle(defaultScreen.titleStringId)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)

        binding.pager.adapter = homePagerAdapter
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val selectedScreen = homePagerAdapter.getItems()[position]
                selectBottomNavigationViewMenuItem(selectedScreen.menuItemId)
                setTitle(selectedScreen.titleStringId)
            }
        })

        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

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

    private fun setTitle(id: Int) {
        (activity as MainActivity).supportActionBar?.setTitle(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}