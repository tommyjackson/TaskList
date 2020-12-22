package com.tjackapps.data.manager

import com.tjackapps.data.model.Group
import com.tjackapps.data.model.Task
import com.tjackapps.data.room.GroupDao
import com.tjackapps.data.room.TaskDao
import io.reactivex.Single

class DatabaseManager internal constructor(
    private val groupDao: GroupDao,
    private val taskDao: TaskDao
) {

    fun getGroups(): Single<List<Group>> {
        return groupDao.getGroups()
    }

    fun addGroup(group: Group): Single<Long> {
        return groupDao.addGroup(group)
    }

    fun updateGroup(group: Group): Single<Int> {
        return groupDao.updateGroup(group)
    }

    fun deleteGroup(group: Group): Single<Int> {
        return groupDao.deleteGroup(group)
    }

    fun deleteTasksFromGroup(group: Group): Single<Int> {
        return taskDao.deleteTasksFromGroup(group.groupId)
    }

    fun getTasksForGroup(groupId: Int): Single<List<Task>> {
        return taskDao.getTasksForGroup(groupId)
    }

    fun addTask(task: Task): Single<Long> {
        return taskDao.addTask(task)
    }

    fun updateTask(task: Task): Single<Int> {
        return taskDao.updateTask(task)
    }

    fun completeTask(taskId: Int, completed: Boolean): Single<Int> {
        return taskDao.completeTask(taskId, completed)
    }

    fun deleteTask(task: Task): Single<Int> {
        return taskDao.deleteTask(task)
    }
}