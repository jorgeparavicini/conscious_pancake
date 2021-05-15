package ch.ffhs.conscious_pancake.ui.game

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ch.ffhs.conscious_pancake.databinding.FragmentGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameFragment: Fragment() {
    private val viewModel: GameViewModel by viewModels()

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}