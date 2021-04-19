package ch.ffhs.conscious_pancake.ui.lobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LobbyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)

        val adapter = LobbyAdapter()

        viewModel.games.observe(viewLifecycleOwner) {
            it.let {
                if (it.status == Status.ERROR) {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                } else {
                    Timber.i("Updated range: ${viewModel.updatedRange.first} - ${viewModel.updatedRange.last}")
                    adapter.setData(it.data!!, viewModel.updatedRange)
                }
            }
        }

        return binding.apply {
            val lm = LinearLayoutManager(activity)
            lobbyList.layoutManager = lm
            lobbyList.adapter = adapter
            lobbyList.addItemDecoration(DividerItemDecoration(lobbyList.context, lm.orientation))
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}