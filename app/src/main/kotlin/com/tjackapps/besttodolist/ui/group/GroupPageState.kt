package com.tjackapps.besttodolist.ui.group

import com.tjackapps.data.model.Group

sealed class GroupPageState {
    object Loading : GroupPageState()

    data class Content(
        val groups: List<Group>
    ) : GroupPageState()

    object Empty : GroupPageState()

    object Error : GroupPageState()
}
