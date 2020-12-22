package com.tjackapps.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tjackapps.data.model.Group
import com.tjackapps.data.model.PriorityConverter
import com.tjackapps.data.model.Task

@Database(entities = [Group::class, Task::class], version = 1)
@TypeConverters(PriorityConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun taskDao(): TaskDao
}