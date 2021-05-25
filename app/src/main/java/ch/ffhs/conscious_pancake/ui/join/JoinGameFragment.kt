package ch.ffhs.conscious_pancake.ui.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.FragmentJoinGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinGameFragment : Fragment(R.layout.fragment_join_game) {

    // TODO: Somehow hide edit menu button
    private var _binding: FragmentJoinGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JoinGameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinGameBinding.inflate(inflater, container, false)
        val adapter = JoinGameAdapter {
            viewModel.joinLobby(it.id) { id -> navigateToLobby(id) }
        }

        viewModel.apply {
            errorMessage.observe(viewLifecycleOwner) {
                it?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    consumeErrorMessage()
                }
            }

            lobbies.observe(viewLifecycleOwner) {
                it?.let {
                    adapter.setData(it, updatedRange)
                }
            }

            isLoading.observe(viewLifecycleOwner) {
                binding.openGamesRefresh.isRefreshing = it
            }

            binding.openGamesRefresh.setOnRefreshListener {
                reloadLobbies()
            }

            binding.joinGameLoadMore.setOnClickListener {
                loadMoreLobbies()
            }
        }

        return binding.apply {
            joinGameViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner

            val lm = LinearLayoutManager(activity)
            joinGameOpenGames.layoutManager = lm
            joinGameOpenGames.adapter = adapter
            joinGameOpenGames.addItemDecoration(
                DividerItemDecoration(
                    joinGameOpenGames.context, lm.orientation
                )
            )

            joinGame.setOnClickListener {
                val lobbyId = joinGameCodeInput.text?.trim()?.toString()
                if (lobbyId == null) {
                    Toast.makeText(
                        requireContext(), "Enter the Lobby code to join a lobby", Toast.LENGTH_LONG
                    ).show()
                } else {
                    viewModel.joinLobby(lobbyId) { id -> navigateToLobby(id) }
                }
            }
        }.root
    }

    private fun navigateToLobby(lobbyId: String) {
        requireView().findNavController()
                .navigate(JoinGameFragmentDirections.actionJoinGameFragmentToLobbyFragment(lobbyId))
    }
}