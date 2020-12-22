package com.tjackapps.besttodolist.ui.group

import com.tjackapps.data.model.Group

interface GroupItemCallback {
    fun onGroupClicked(group: Group)
}