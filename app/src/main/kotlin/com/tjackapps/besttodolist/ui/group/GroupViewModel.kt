package com.tjackapps.besttodolist.ui.group

import androidx.lifecycle.ViewModel
import com.tjackapps.besttodolist.helper.extensions.plusAssign
import com.tjackapps.data.manager.DatabaseManager
import com.tjackapps.data.model.Group
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class GroupViewModel @Inject constructor(
        private val databaseManager: DatabaseManager
): ViewModel() {

    private var compositeDisposable = CompositeDisposable()

    private val fragmentState = BehaviorSubject.create<GroupPageState>()
    fun fragmentState(): Observable<GroupPageState> = fragmentState.hide()

    private val sheetAction = PublishSubject.create<GroupSheetAction>()
    fun sheetAction(): Observable<GroupSheetAction> = sheetAction.hide()

    private var groups = emptyList<Group>()

    /**
     * Loads all of the groups in the database
     */
    fun getGroups() {
        showLoading()

        compositeDisposable += loadGroups()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    groups = it
                    if (groups.isEmpty()) {
                        fragmentState.onNext(GroupPageState.Empty)
                    } else {
                        fragmentState.onNext(GroupPageState.Content(it))
                    }
                }, {
                    Timber.e(GroupError.GROUP_LOAD_FAILURE)
                    fragmentState.onNext(GroupPageState.Error)
                })
    }

    /**
     * Adds a group to the database
     */
    fun addGroup(group: Group) {

        compositeDisposable += databaseManager
                .addGroup(group)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    sheetAction.onNext(GroupSheetAction.SaveGroupSuccess)
                }, {
                    Timber.e(GroupError.GROUP_ADD_FAILURE)
                    sheetAction.onNext(GroupSheetAction.SaveGroupFailure)
                })
    }

    /**
     * Updates a group in the database
     */
    fun updateGroup(group: Group) {

        compositeDisposable += databaseManager
                .updateGroup(group)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    sheetAction.onNext(GroupSheetAction.SaveGroupSuccess)
                }, {
                    Timber.e(GroupError.GROUP_UPDATE_FAILURE)
                    sheetAction.onNext(GroupSheetAction.SaveGroupFailure)
                })
    }

    /**
     * Deletes a group in the database and all of its associated tasks
     */
    fun deleteGroup(group: Group) {
        showLoading()

        compositeDisposable += databaseManager
                .deleteTasksFromGroup(group)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    databaseManager.deleteGroup(group)
                }
                .flatMap {
                    loadGroups()
                }
                .subscribe({
                    groups = it
                    if (groups.isEmpty()) {
                        fragmentState.onNext(GroupPageState.Empty)
                    } else {
                        fragmentState.onNext(GroupPageState.Content(it))
                    }
                }, {
                    Timber.e(GroupError.GROUP_DELETE_FAILURE)
                    fragmentState.onNext(GroupPageState.Error)
                })
    }

    private fun loadGroups(): Single<List<Group>> {
        groups = emptyList()
        return databaseManager.getGroups()
    }

    fun getGroupFromIndex(index: Int): Group {
        return groups[index]
    }

    private fun showLoading() {
        fragmentState.onNext(GroupPageState.Loading)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}