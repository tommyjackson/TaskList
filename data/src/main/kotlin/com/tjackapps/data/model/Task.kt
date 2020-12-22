package com.tjackapps.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Task(
    @PrimaryKey (autoGenerate = true) var taskId: Int = 0,
    val groupId: Int,
    val name: String,
    val description: String,
    val completed: Boolean,
    val priority: Priority
) : Parcelable
