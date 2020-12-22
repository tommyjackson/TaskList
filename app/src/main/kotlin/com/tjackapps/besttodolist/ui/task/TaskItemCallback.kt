package com.tjackapps.besttodolist.ui.task

import com.tjackapps.data.model.Task

interface TaskItemCallback {
    fun onTaskChecked(task: Task, index: Int, completed: Boolean)
}