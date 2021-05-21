package ch.ffhs.conscious_pancake.ui.lobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import ch.ffhs.conscious_pancake.databinding.FragmentLobbyBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LobbyFragment : Fragment() {

    private var _binding: FragmentLobbyBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: LobbyViewModel by viewModels()

    private var hasLeftLobby = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)

        viewModel.apply {
            lobbyDestroyed.observe(viewLifecycleOwner) {
                if (it) {
                    Toast.makeText(requireContext(), "Lobby closed by host", Toast.LENGTH_LONG)
                            .show()
                    hasLeftLobby = true
                    requireView().findNavController().navigateUp()
                }
            }

            errorMessage.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }

            gameStarted.observe(viewLifecycleOwner) {
                it?.let {
                    navigateToGame(it)
                }
            }
        }

        return binding.apply {
            lobbyViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner

            lobbyStart.setOnClickListener {
                viewModel.startGame()
            }
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (!hasLeftLobby) {
            viewModel.leaveLobby()
        }
    }

    private fun navigateToGame(gameId: String) {
        Timber.i(" Navigating to game")
        requireView().findNavController()
                .navigate(
                    LobbyFragmentDirections.actionLobbyFragmentToGameFragment(
                        gameId,
                        viewModel.isHost
                    )
                )
    }
}