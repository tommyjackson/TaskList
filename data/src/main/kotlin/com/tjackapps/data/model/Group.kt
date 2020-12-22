package com.tjackapps.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Group(
    @PrimaryKey (autoGenerate = true) var groupId: Int = 0,
    val name: String,
    val description: String
) : Parcelable
