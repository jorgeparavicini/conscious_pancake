package ch.ffhs.conscious_pancake.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ch.ffhs.conscious_pancake.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private val viewModel: HistoryViewModel by viewModels()

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val adapter = HistoryAdapter()

        viewModel.apply {
            errorMessage.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }

            history.observe(viewLifecycleOwner) {
                it.let {
                    adapter.setData(it, null)
                }
            }

            isLoading.observe(viewLifecycleOwner) {
                binding.historySwipeRefresh.isRefreshing = it
            }

            binding.historySwipeRefresh.setOnRefreshListener {
                reloadGames()
            }
            binding.historyLoadMore.setOnClickListener { loadMoreGames() }
        }



        return binding.apply {
            historyViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner

            val lm = LinearLayoutManager(activity)
            historyList.layoutManager = lm
            historyList.adapter = adapter
            historyList.addItemDecoration(DividerItemDecoration(historyList.context, lm.orientation))
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}