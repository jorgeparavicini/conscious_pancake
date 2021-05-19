package ch.ffhs.conscious_pancake.ui.games

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.ffhs.conscious_pancake.databinding.LobbyRowItemBinding
import ch.ffhs.conscious_pancake.vo.Game

typealias OnGameClickedListener = (game: Game) -> Unit

class GamesAdaptor(private val itemClicked: OnGameClickedListener) : RecyclerView.Adapter<GamesAdaptor.LobbyViewHolder>() {

    private var data = listOf<Game>()

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        return LobbyViewHolder.from(parent, itemClicked)
    }

    fun setData(value: List<Game>, updateRange: IntRange?) {
        val old = data
        data = value
        when {
            updateRange == null -> {
                notifyDataSetChanged()
            }
            old.size >= data.size -> {
                notifyItemRangeRemoved(0, old.size)
            }
            else -> {
                notifyItemRangeChanged(updateRange.first, updateRange.last)
            }
        }
    }


    class LobbyViewHolder(
        private val binding: LobbyRowItemBinding,
        private val clicked: OnGameClickedListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(item: Game) {
            binding.apply {
                player1Name.text = item.player1?.username
                player2Name.text = item.player2?.username
                playGameButton.setOnClickListener { clicked(item) }
            }
        }

        companion object {

            fun from(parent: ViewGroup, clicked: OnGameClickedListener): LobbyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LobbyRowItemBinding.inflate(layoutInflater, parent, false)
                return LobbyViewHolder(binding, clicked)
            }
        }
    }
}