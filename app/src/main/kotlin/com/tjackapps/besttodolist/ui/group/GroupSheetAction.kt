package com.tjackapps.besttodolist.ui.group

sealed class GroupSheetAction {
    object SaveGroupSuccess : GroupSheetAction()
    object SaveGroupFailure : GroupSheetAction()
}
