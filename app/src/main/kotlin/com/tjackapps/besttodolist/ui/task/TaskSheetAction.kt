package com.tjackapps.besttodolist.ui.task

sealed class TaskSheetAction {
    object SaveTaskSuccess : TaskSheetAction()
    object SaveTaskFailure : TaskSheetAction()
}
