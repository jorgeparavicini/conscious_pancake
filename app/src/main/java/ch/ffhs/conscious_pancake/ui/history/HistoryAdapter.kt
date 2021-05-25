package ch.ffhs.conscious_pancake.ui.history

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.HistoryRowItemBinding
import ch.ffhs.conscious_pancake.vo.Game
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var data = listOf<Game>()

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder.from(parent)
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


    class HistoryViewHolder(private val binding: HistoryRowItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(item: Game) {
            binding.apply {
                val user = Firebase.auth.currentUser!!.uid
                val winnerUser = if (item.winner == item.hostId) item.player1 else item.player2
                val tint = if (winnerUser?.profilePictureUri == null)
                    ColorStateList.valueOf(
                        ContextCompat.getColor(winnerPicture.context, R.color.picture_tint))
                else null
                ImageViewCompat.setImageTintList(winnerPicture, tint)
                winnerUsername.text = winnerUser?.username
                winnerPicture.setImageURI(winnerUser?.profilePictureUri)
                historyTurns.text = item.remoteMoves.count().toString()
                if (item.winner == user) {
                    wonLost.setText(R.string.won)
                    wonLost.setTextColor(
                        ContextCompat.getColor(
                            binding.wonLost.context, R.color.success_green
                        )
                    )
                } else {
                    wonLost.setText(R.string.lost)
                    wonLost.setTextColor(
                        ContextCompat.getColor(
                            binding.wonLost.context, R.color.error_red
                        )
                    )
                }
            }
        }

        companion object {

            fun from(parent: ViewGroup): HistoryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HistoryRowItemBinding.inflate(layoutInflater, parent, false)
                return HistoryViewHolder(binding)
            }
        }
    }


}