package com.tjackapps.data.room

import androidx.room.*
import com.tjackapps.data.model.Task
import io.reactivex.Single

@Dao
interface TaskDao {

    /**
     * Gets all of the tasks for a group
     *
     * @param groupId: The id of the group to get tasks from
     */
    @Query("SELECT * FROM task WHERE groupId = :groupId")
    fun getTasksForGroup(groupId: Int): Single<List<Task>>

    /**
     * Adds a task to the database
     *
     * @param task: The task to add
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTask(task: Task): Single<Long>

    /**
     * Updates a task in the database
     *
     * @param task: The task to update
     */
    @Update
    fun updateTask(task: Task): Single<Int>

    /**
     * Sets the completed value for a task in the database
     *
     * @param taskId: The task to update
     * @param completed: The task's completed state
     */
    @Query("UPDATE Task SET completed = :completed WHERE taskId = :taskId")
    fun completeTask(taskId: Int, completed: Boolean): Single<Int>

    /**
     * Deletes a task from the database
     *
     * @param task: The task to delete
     */
    @Delete
    fun deleteTask(task: Task): Single<Int>

    /**
     * Deletes all tasks from a group
     *
     * @param groupId: The id of the group to delete tasks from
     */
    @Query("DELETE FROM task WHERE groupId = :groupId")
    fun deleteTasksFromGroup(groupId: Int): Single<Int>
}