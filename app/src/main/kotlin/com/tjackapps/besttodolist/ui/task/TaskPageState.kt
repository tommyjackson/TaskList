package com.tjackapps.besttodolist.ui.task

import com.tjackapps.data.model.Task

sealed class TaskPageState {
    object Loading : TaskPageState()

    data class Content(
        val tasks: List<Task>
    ) : TaskPageState()

    object Error : TaskPageState()
}
