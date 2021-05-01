package ch.ffhs.conscious_pancake.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ch.ffhs.conscious_pancake.databinding.FragmentGamesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GamesFragment : Fragment() {
    private val viewModel: GamesViewModel by viewModels()

    private var _binding: FragmentGamesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGamesBinding.inflate(inflater, container, false)

        val adapter = GamesAdaptor()

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        return binding.apply {
            lobbyViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner

            val lm = LinearLayoutManager(activity)
            lobbyList.layoutManager = lm
            lobbyList.adapter = adapter
            lobbyList.addItemDecoration(DividerItemDecoration(lobbyList.context, lm.orientation))

            viewModel.isLoading.observe(viewLifecycleOwner) {
                lobbySwipeRefresh.isRefreshing = it
            }

            viewModel.lobbies.observe(viewLifecycleOwner) {
                it.let {
                    adapter.setData(it, null)
                }
            }

            lobbySwipeRefresh.setOnRefreshListener {
                viewModel.reloadGames()
            }

            loadMore.setOnClickListener { viewModel.loadMoreGames() }
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}