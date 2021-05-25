package ch.ffhs.conscious_pancake.ui.games

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.GamesRowItemBinding
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
        private val binding: GamesRowItemBinding,
        private val clicked: OnGameClickedListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(item: Game) {
            binding.apply {
                val tint = ColorStateList.valueOf(
                    ContextCompat.getColor(player1Image.context, R.color.picture_tint)
                )
                val player1Tint = if (item.player1?.profilePictureUri == null) tint else null
                val player2Tint = if (item.player2?.profilePictureUri == null) tint else null
                ImageViewCompat.setImageTintList(player1Image, player1Tint)
                ImageViewCompat.setImageTintList(player2Image, player2Tint)

                player1Image.setImageURI(item.player1?.profilePictureUri)
                player1Name.text = item.player1?.username
                player2Image.setImageURI(item.player2?.profilePictureUri)
                player2Name.text = item.player2?.username
                gamesTurns.text = item.remoteMoves.count().toString()
                playGameButton.setOnClickListener { clicked(item) }
            }
        }

        companion object {

            fun from(parent: ViewGroup, clicked: OnGameClickedListener): LobbyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GamesRowItemBinding.inflate(layoutInflater, parent, false)
                return LobbyViewHolder(binding, clicked)
            }
        }
    }
}