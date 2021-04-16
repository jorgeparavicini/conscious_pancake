package ch.ffhs.conscious_pancake.ui.host

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.FragmentHostGameBinding
import ch.ffhs.conscious_pancake.ui.main.MainActivity

class HostGameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHostGameBinding.inflate(inflater, container, false)
        return binding.root
    }
}