package com.tjackapps.data.room

import androidx.room.*
import com.tjackapps.data.model.Task
import io.reactivex.Single

@Dao
interface TaskDao {

    @Query("SELECT * FROM task WHERE groupId = :groupId")
    fun getTasksForGroup(groupId: Int): Single<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTask(task: Task): Single<Long>

    @Update
    fun updateTask(task: Task): Single<Int>

    @Query("UPDATE Task SET completed = :completed WHERE taskId = :taskId")
    fun completeTask(taskId: Int, completed: Boolean): Single<Int>

    @Delete
    fun deleteTask(task: Task): Single<Int>

    @Query("DELETE FROM task WHERE groupId = :groupId")
    fun deleteTasksFromGroup(groupId: Int): Single<Int>
}