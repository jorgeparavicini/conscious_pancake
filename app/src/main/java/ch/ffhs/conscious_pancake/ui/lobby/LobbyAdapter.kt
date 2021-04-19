package ch.ffhs.conscious_pancake.ui.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.LobbyRowItemBinding
import ch.ffhs.conscious_pancake.vo.Game

class LobbyAdapter : RecyclerView.Adapter<LobbyAdapter.LobbyViewHolder>() {

    var data = listOf<Game>()
        set(value) {
            field = value
            notifyDataSetChanged()
            // TODO: Could be improved - don't update everything
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        return LobbyViewHolder.from(parent)
    }


    class LobbyViewHolder(private val binding: LobbyRowItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(item: Game) {
            binding.apply {
                player1Name.text = item.player1Id
                player2Name.text = item.player2Id
            }
        }

        companion object {

            fun from(parent: ViewGroup): LobbyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LobbyRowItemBinding.inflate(layoutInflater, parent, false)
                return LobbyViewHolder(binding)
            }
        }
    }
}