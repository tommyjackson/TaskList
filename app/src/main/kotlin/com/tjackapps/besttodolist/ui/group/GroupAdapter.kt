package com.tjackapps.besttodolist.ui.group

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tjackapps.besttodolist.helper.extensions.getLayoutInflater
import com.tjackapps.data.model.Group
import com.tjackapps.besttodolist.databinding.GroupItemBinding


class GroupAdapter(
    private val callback: GroupItemCallback,
    private val groups: List<Group>
    ) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    class GroupViewHolder(view: View, binding: GroupItemBinding) : RecyclerView.ViewHolder(view) {
        val name: AppCompatTextView = binding.name
        val description: AppCompatTextView = binding.description
        val layout: ConstraintLayout = binding.layout
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = GroupItemBinding.inflate(viewGroup.getLayoutInflater(), viewGroup, false)
        return GroupViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(viewHolder: GroupViewHolder, position: Int) {

        val group = groups[position]

        viewHolder.name.text = group.name
        viewHolder.description.text = group.description
        viewHolder.layout.setOnClickListener {
            callback.onGroupClicked(group)
        }
    }

    override fun getItemCount() = groups.size
}
