package ch.ffhs.conscious_pancake.ui.join

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.OpenGamesRowItemBinding
import ch.ffhs.conscious_pancake.vo.Lobby

typealias OnItemClickedListener = (lobby: Lobby) -> Unit

class JoinGameAdapter(private val itemClicked: OnItemClickedListener) :
        RecyclerView.Adapter<JoinGameAdapter.JoinGameViewHolder>() {

    private var data = listOf<Lobby>()

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: JoinGameViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinGameViewHolder {
        return JoinGameViewHolder.from(parent, itemClicked)
    }

    fun setData(value: List<Lobby>, updateRange: IntRange?) {
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

    class JoinGameViewHolder(
        private val binding: OpenGamesRowItemBinding,
        private val clicked: OnItemClickedListener
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(item: Lobby) {
            binding.apply {
                val tint = if (item.host?.profilePictureUri == null)
                    ColorStateList.valueOf(
                        ContextCompat.getColor(openGamesPicture.context, R.color.picture_tint)
                    )
                else null

                ImageViewCompat.setImageTintList(openGamesPicture, tint)
                openGamesPicture.setImageURI(item.host?.profilePictureUri)
                openGamesUsername.text = item.host?.username
                openGamesSize.text = item.gameSize.displayName
                openGamesDuration.text = item.turnDuration.displayName
                openGamesJoin.setOnClickListener { clicked(item) }
            }
        }

        companion object {

            fun from(
                parent: ViewGroup,
                clicked: OnItemClickedListener
            ): JoinGameViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = OpenGamesRowItemBinding.inflate(layoutInflater, parent, false)
                return JoinGameViewHolder(binding, clicked)
            }
        }
    }
}