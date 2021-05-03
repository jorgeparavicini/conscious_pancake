package ch.ffhs.conscious_pancake.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        // TODO: This needs some serious rework
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
                enemyUserName.text =
                    if (user == item.player1Id) item.player1?.username else item.player2?.username
                if (item.winner == user) {
                    wonLost.setText(R.string.won)
                    wonLost.setTextColor(
                        ContextCompat.getColor(
                            binding.enemyUserName.context, R.color.success_green
                        )
                    )
                } else {
                    wonLost.setText(R.string.lost)
                    wonLost.setTextColor(
                        ContextCompat.getColor(
                            binding.enemyUserName.context, R.color.error_red
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