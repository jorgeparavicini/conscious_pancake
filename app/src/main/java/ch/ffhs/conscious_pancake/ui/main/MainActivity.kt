package ch.ffhs.conscious_pancake.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.ffhs.conscious_pancake.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
    }
}