package ch.ffhs.conscious_pancake.activities.lobby

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.ffhs.conscious_pancake.R

class LobbyFragment : Fragment(R.layout.activity_lobby) {
    private val data: Array<LobbyItem> =
        arrayOf(
            LobbyItem("Test 1"),
            LobbyItem("Test 2"),
            LobbyItem("Test 3"),
            LobbyItem("Test 4"),
            LobbyItem("Test 5"),
            LobbyItem("Test 5"),
            LobbyItem("Test 5"),
            LobbyItem("Test 5"),
            LobbyItem("Test 5"),
            LobbyItem("Test 5")


        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = view.findViewById<RecyclerView>(R.id.lobby_list)
        list.apply {
            setHasFixedSize(true)
            val lm = LinearLayoutManager(activity)
            layoutManager = lm
            adapter = LobbyAdapter(data)
            addItemDecoration(DividerItemDecoration(list.context, lm.orientation))
        }
    }
}