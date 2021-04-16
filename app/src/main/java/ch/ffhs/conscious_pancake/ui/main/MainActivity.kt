package ch.ffhs.conscious_pancake.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.titleFragment, R.id.homeFragment))
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_host) as NavHostFragment
        navController = navHostFragment.navController

        binding.toolbar.setupWithNavController(
            navController, appBarConfiguration
        )
        setSupportActionBar(binding.toolbar)
    }
}