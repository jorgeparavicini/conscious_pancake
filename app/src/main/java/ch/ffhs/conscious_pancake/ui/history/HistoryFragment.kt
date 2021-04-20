package ch.ffhs.conscious_pancake.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.ffhs.conscious_pancake.R
import timber.log.Timber

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private val data: Array<HistoryItem> = arrayOf(
        HistoryItem("Enemy 1", true),
        HistoryItem("Enemy 2", true),
        HistoryItem("Enemy 3", false),
        HistoryItem("Enemy 4", true),
        HistoryItem("Enemy 5", false),
        HistoryItem("Enemy 6", false),
        HistoryItem("Enemy 7", true)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.i("History Created")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = view.findViewById<RecyclerView>(R.id.history_list)
        list.apply {
            setHasFixedSize(true)
            val lm = LinearLayoutManager(activity)
            layoutManager = lm
            adapter = HistoryAdapter(data)
            addItemDecoration(DividerItemDecoration(list.context, lm.orientation))
        }
    }
}