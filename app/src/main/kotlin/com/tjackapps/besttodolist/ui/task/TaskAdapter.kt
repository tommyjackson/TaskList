package com.tjackapps.besttodolist.ui.task

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tjackapps.besttodolist.R
import com.tjackapps.besttodolist.ui.misc.getLayoutInflater
import com.tjackapps.besttodolist.databinding.TaskItemBinding
import com.tjackapps.data.model.Priority
import com.tjackapps.data.model.Task

class TaskAdapter(
    private val callback: TaskItemCallback,
    private val tasks: List<Task>
    ) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View, binding: TaskItemBinding) : RecyclerView.ViewHolder(view) {
        val name: AppCompatTextView = binding.name
        val description: AppCompatTextView = binding.description
        val priorityIndicator: View = binding.priorityIndicator
        val completed: AppCompatCheckBox = binding.completed
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(viewGroup.getLayoutInflater(), viewGroup, false)
        return TaskViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(viewHolder: TaskViewHolder, position: Int) {

        val task = tasks[position]

        viewHolder.name.text = task.name
        viewHolder.description.text = task.description
        viewHolder.completed.isChecked = task.completed
        viewHolder.completed.setOnCheckedChangeListener { _, b ->
            callback.onTaskChecked(task, position, b)
        }
        val priorityColor = when (task.priority) {
            Priority.LOW -> R.color.green
            Priority.MEDIUM -> R.color.yellow
            Priority.HIGH -> R.color.red
        }
        viewHolder.priorityIndicator.backgroundTintList = ContextCompat.getColorStateList(viewHolder.priorityIndicator.context, priorityColor)
    }

    override fun getItemCount() = tasks.size
}
