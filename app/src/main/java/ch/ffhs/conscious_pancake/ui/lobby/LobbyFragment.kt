package ch.ffhs.conscious_pancake.ui.lobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.FragmentLobbyBinding
import ch.ffhs.conscious_pancake.vo.Status
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LobbyFragment : Fragment() {
    private val viewModel: LobbyViewModel by viewModels()

    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)

        val adapter = LobbyAdapter()

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