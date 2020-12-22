package com.tjackapps.data.model

import androidx.room.TypeConverter

class PriorityConverter {

    @TypeConverter
    fun fromPriority(priority: Priority): Int {
        return when (priority) {
            Priority.LOW -> 0
            Priority.MEDIUM -> 1
            Priority.HIGH -> 2
        }
    }

    @TypeConverter
    fun toPriority(priority: Int): Priority {
        return when (priority) {
            0 -> Priority.LOW
            1 -> Priority.MEDIUM
            2 -> Priority.HIGH
            else -> Priority.LOW
        }
    }
}