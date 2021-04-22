package ch.ffhs.conscious_pancake.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ch.ffhs.conscious_pancake.R
import ch.ffhs.conscious_pancake.databinding.HistoryRowItemBinding
import ch.ffhs.conscious_pancake.vo.Game

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var data = listOf<Game>()

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        /*holder.enemyUserNameTextView.text = dataSet[position].enemyUserName
        if (dataSet[position].won) {
            holder.wonLostTextView.setText(R.string.won)
            holder.wonLostTextView.setTextColor(
                ContextCompat.getColor(
                    holder.enemyUserNameTextView.context, R.color.success_green
                )
            )
        } else {
            holder.wonLostTextView.setText(R.string.lost)
            holder.wonLostTextView.setTextColor(
                ContextCompat.getColor(
                    holder.enemyUserNameTextView.context, R.color.error_red
                )
            )
        }*/
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
                // TODO: too much to think right now
                enemyUserName.text = item.player2Id
               /* if (dataSet[position].won) {
                    holder.wonLostTextView.setText(R.string.won)
                    holder.wonLostTextView.setTextColor(
                        ContextCompat.getColor(
                            holder.enemyUserNameTextView.context, R.color.success_green
                        )
                    )
                } else {
                    holder.wonLostTextView.setText(R.string.lost)
                    holder.wonLostTextView.setTextColor(
                        ContextCompat.getColor(
                            holder.enemyUserNameTextView.context, R.color.error_red
                        )
                    )
                }*/
            }
        }
        //val enemyUserNameTextView: TextView = view.findViewById(R.id.enemy_user_name)
        //val wonLostTextView: TextView = view.findViewById(R.id.won_lost)

        companion object {
            fun from(parent: ViewGroup): HistoryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HistoryRowItemBinding.inflate(layoutInflater, parent, false)
                return HistoryViewHolder(binding)
            }
        }
    }


}