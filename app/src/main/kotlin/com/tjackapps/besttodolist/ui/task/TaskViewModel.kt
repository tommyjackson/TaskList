package com.tjackapps.besttodolist.ui.task

import androidx.lifecycle.ViewModel
import com.tjackapps.besttodolist.ui.misc.plusAssign
import com.tjackapps.data.manager.DatabaseManager
import com.tjackapps.data.model.Task
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class TaskViewModel @Inject constructor(
    private val databaseManager: DatabaseManager
): ViewModel() {

    private var compositeDisposable = CompositeDisposable()

    private val fragmentState = BehaviorSubject.create<TaskPageState>()
    fun fragmentState(): Observable<TaskPageState> = fragmentState.hide()

    private val sheetAction = PublishSubject.create<TaskSheetAction>()
    fun sheetAction(): Observable<TaskSheetAction> = sheetAction.hide()

    private var tasks = emptyList<Task>()

    var groupId = -1

    fun getTasksForGroup(groupId: Int) {
        showLoading()
        this.groupId = groupId

        compositeDisposable += loadTasksForGroup()
            .subscribeOn(Schedulers.io())
            .subscribe({
                tasks = it
                fragmentState.onNext(TaskPageState.Content(it))
            }, {
                Timber.e(TaskError.TASK_LOAD_FAILURE)
                fragmentState.onNext(TaskPageState.Error)
            })
    }

    fun addTask(task: Task) {

        compositeDisposable += databaseManager
            .addTask(task)
            .subscribeOn(Schedulers.io())
            .subscribe({
                sheetAction.onNext(TaskSheetAction.SaveTaskSuccess)
            }, {
                Timber.e(TaskError.TASK_ADD_FAILURE)
                sheetAction.onNext(TaskSheetAction.SaveTaskFailure)
            })
    }

    fun updateTask(task: Task) {

        compositeDisposable += databaseManager
            .updateTask(task)
            .subscribeOn(Schedulers.io())
            .subscribe({
                sheetAction.onNext(TaskSheetAction.SaveTaskSuccess)
            }, {
                Timber.e(TaskError.TASK_UPDATE_FAILURE)
                sheetAction.onNext(TaskSheetAction.SaveTaskFailure)
            })
    }

    fun completeTask(taskId: Int, completed: Boolean) {
        showLoading()

        compositeDisposable += databaseManager
            .completeTask(taskId, completed)
            .subscribeOn(Schedulers.io())
            .flatMap {
                loadTasksForGroup()
            }
            .subscribe({
                tasks = it
                fragmentState.onNext(TaskPageState.Content(it))
            }, {
                Timber.e(TaskError.TASK_COMPLETE_FAILURE)
                fragmentState.onNext(TaskPageState.Error)
            })
    }

    fun deleteTask(task: Task) {
        showLoading()

        compositeDisposable += databaseManager
            .deleteTask(task)
            .subscribeOn(Schedulers.io())
            .flatMap {
                loadTasksForGroup()
            }
            .subscribe({
                tasks = it
                fragmentState.onNext(TaskPageState.Content(it))
            }, {
                Timber.e(TaskError.TASK_DELETE_FAILURE)
                fragmentState.onNext(TaskPageState.Error)
            })
    }

    private fun loadTasksForGroup(): Single<List<Task>> {
        tasks = emptyList()
        return databaseManager.getTasksForGroup(groupId)
    }

    fun getTaskFromIndex(index: Int): Task {
        return tasks[index]
    }

    private fun showLoading() {
        fragmentState.onNext(TaskPageState.Loading)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}