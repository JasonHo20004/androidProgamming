package com.example.to_dolistapp.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistapp.R
import com.example.to_dolistapp.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// TaskAdapter.kt
class TaskAdapter(private var items: List<Task>)
    : RecyclerView.Adapter<TaskAdapter.VH>() {

    inner class VH(view: View): RecyclerView.ViewHolder(view) {
        private val tvName = view.findViewById<TextView>(R.id.tvName)
        private val tvDue  = view.findViewById<TextView>(R.id.tvDue)

        private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        fun bind(t: Task) {
            tvName.text = t.name
            // convert timestamp (Long) to Date, then format
            tvDue.text = dateFormatter.format(Date(t.due))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task, parent, false)
        )

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    fun update(newList: List<Task>) {
        items = newList
        notifyDataSetChanged()
    }
}