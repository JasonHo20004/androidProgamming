package com.example.to_dolistapp.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolistapp.R
import com.example.to_dolistapp.model.Task
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(
    private var items: List<Task>,
    private val onTaskClick: (Task) -> Unit,
    private val onTaskDelete: (Task) -> Unit,
    private val onPriorityChange: (Task, Task.Priority) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardView = view as MaterialCardView
        private val cbComplete = view.findViewById<CheckBox>(R.id.cbComplete)
        private val tvName = view.findViewById<TextView>(R.id.tvName)
        private val chipDate = view.findViewById<Chip>(R.id.chipDate)
        private val chipPriority = view.findViewById<Chip>(R.id.chipPriority)
        private val btnMenu = view.findViewById<ImageButton>(R.id.btnMenu)

        private val dateFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())

        fun bind(task: Task) {
            // Set task name
            tvName.text = task.name

            // Handle completed tasks
            cbComplete.isChecked = task.isCompleted
            if (task.isCompleted) {
                tvName.paintFlags = tvName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                cardView.alpha = 0.7f
            } else {
                tvName.paintFlags = tvName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                cardView.alpha = 1.0f
            }

            // Set date chip
            chipDate.text = dateFormatter.format(Date(task.due))

            // Set priority chip
            chipPriority.text = task.priority.name.capitalize(Locale.ROOT)
            val colorResId = when (task.priority) {
                Task.Priority.HIGH -> R.color.priority_high
                Task.Priority.MEDIUM -> R.color.priority_medium
                Task.Priority.LOW -> R.color.priority_low
            }

            // Update priority chip colors
            val colorInt = ContextCompat.getColor(itemView.context, colorResId)
            chipPriority.setTextColor(colorInt)
            chipPriority.chipBackgroundColor = ContextCompat.getColorStateList(
                itemView.context,
                when (task.priority) {
                    Task.Priority.HIGH -> R.color.priority_high_bg
                    Task.Priority.MEDIUM -> R.color.priority_medium_bg
                    Task.Priority.LOW -> R.color.priority_low_bg
                }
            )

            // Set click listeners
            cbComplete.setOnClickListener {
                onTaskClick(task)
            }

            itemView.setOnClickListener {
                // Handle item click
                onTaskClick(task)
            }

            btnMenu.setOnClickListener {
                // Show options menu for this task
                showTaskOptions(task)
            }
        }

        private fun showTaskOptions(task: Task) {
            // Create and show a popup menu with options for this task
            val popup = android.widget.PopupMenu(itemView.context, btnMenu)
            popup.menuInflater.inflate(R.menu.task_options_menu, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        // TODO: Implement edit functionality
                        true
                    }
                    R.id.action_delete -> {
                        onTaskDelete(task)  // Notify the activity/fragment about the deletion
                        val newList = items.toMutableList()
                        newList.remove(task)
                        update(newList)
                        true
                    }
                    R.id.action_priority_high -> {
                        updateTaskPriority(task, Task.Priority.HIGH)
                        true
                    }
                    R.id.action_priority_medium -> {
                        updateTaskPriority(task, Task.Priority.MEDIUM)
                        true
                    }
                    R.id.action_priority_low -> {
                        updateTaskPriority(task, Task.Priority.LOW)
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        }

        private fun updateTaskPriority(task: Task, priority: Task.Priority) {
            onPriorityChange(task, priority)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun update(newList: List<Task>) {
        items = newList
        notifyDataSetChanged()
    }
}