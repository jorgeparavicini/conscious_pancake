package ch.ffhs.conscious_pancake.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.FragmentGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameFragment : Fragment() {
    private val viewModel: GameViewModel by viewModels()

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)

        viewModel.apply {
            setOnPieceSelectionChangedHandler { piece, selected ->
                binding.gameView.onPieceSelectionChanged(piece, selected)
            }
            setOnGameResetHandler {
                binding.gameView.onReset()
            }

            turnLabel.observe(viewLifecycleOwner) {
                binding.turnLabel.text = it
            }

            errorMessage.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }

            game.observe(viewLifecycleOwner) { game ->
                if (game == null) return@observe
                if (!binding.gameView.initialized) {
                    binding.gameView.draughts = draughts
                }
            }

            playerLabel.observe(viewLifecycleOwner) {
                binding.playerLabel.text = getString(R.string.player_label, it)
            }
        }

        return binding.apply {
            gameView.setOnCellClickedListener { viewModel.onCellClicked(it) }
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}