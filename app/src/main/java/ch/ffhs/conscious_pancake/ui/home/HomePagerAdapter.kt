package ch.ffhs.conscious_pancake.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomePagerAdapter(private val args: Bundle?, fa: FragmentActivity) :
    FragmentStateAdapter(fa) {

    private val screens = arrayListOf<HomeScreen>()

    fun setItems(screens: List<HomeScreen>) {
        this.screens.apply {
            clear()
            addAll(screens)
            notifyDataSetChanged()
        }
    }

    fun getItems(): List<HomeScreen> {
        return screens
    }

    override fun getItemCount(): Int {
        return screens.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = screens[position].fragment
        fragment.arguments = args
        return fragment
    }
}