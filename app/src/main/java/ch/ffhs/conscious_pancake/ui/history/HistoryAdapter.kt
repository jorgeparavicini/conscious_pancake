package ch.ffhs.conscious_pancake.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ch.ffhs.conscious_pancake.R

class HistoryAdapter(private val dataSet: Array<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val enemyUserNameTextView: TextView = view.findViewById(R.id.enemy_user_name)
        val wonLostTextView: TextView = view.findViewById(R.id.won_lost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.history_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.enemyUserNameTextView.text = dataSet[position].enemyUserName
        if (dataSet[position].won) {
            holder.wonLostTextView.setText(R.string.won)
            holder.wonLostTextView.setTextColor(
                ContextCompat.getColor(
                    holder.enemyUserNameTextView.context,
                    R.color.success_green
                )
            )
        } else {
            holder.wonLostTextView.setText(R.string.lost)
            holder.wonLostTextView.setTextColor(
                ContextCompat.getColor(
                    holder.enemyUserNameTextView.context,
                    R.color.error_red
                )
            )
        }
    }

    override fun getItemCount() = dataSet.size
}