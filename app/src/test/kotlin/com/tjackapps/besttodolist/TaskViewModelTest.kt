package com.tjackapps.besttodolist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import com.tjackapps.besttodolist.ui.task.*
import com.tjackapps.data.manager.DatabaseManager
import com.tjackapps.data.model.Priority
import com.tjackapps.data.model.Task
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test

class TaskViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    private val databaseManager: DatabaseManager = mock()
    private val taskViewModel = TaskViewModel(databaseManager)

    @Test
    fun `verify content state gets emitted after tasks are loaded`() {
        val contentState = TaskPageState.Content(emptyList())

        given {
            databaseManager.getTasksForGroup(any())
        } willReturn {
            Single.just(emptyList())
        }

        taskViewModel.getTasksForGroup(0)
        taskViewModel.fragmentState()
            .test()
            .assertNoErrors()
            .assertNotComplete()
            .assertValue {
                it == contentState
            }
    }

    @Test
    fun `verify content state gets emitted after task is deleted`() {
        val contentState = TaskPageState.Content(emptyList())

        given {
            databaseManager.deleteTask(any())
        } willReturn {
            Single.just(0)
        }

        given {
            databaseManager.deleteTask(any())
        } willReturn {
            Single.just(0)
        }

        given {
            databaseManager.getTasksForGroup(any())
        } willReturn {
            Single.just(emptyList())
        }

        taskViewModel.deleteTask(mockTask)
        taskViewModel.fragmentState()
            .test()
            .assertNoErrors()
            .assertNotComplete()
            .assertValue {
                it == contentState
            }
    }

    @Test
    fun `verify content state gets emitted after task is completed`() {
        val contentState = TaskPageState.Content(emptyList())

        given {
            databaseManager.completeTask(any(), any())
        } willReturn {
            Single.just(0)
        }

        given {
            databaseManager.getTasksForGroup(any())
        } willReturn {
            Single.just(emptyList())
        }

        taskViewModel.completeTask(0, true)
        taskViewModel.fragmentState()
            .test()
            .assertNoErrors()
            .assertNotComplete()
            .assertValue {
                it == contentState
            }
    }

    @Test
    fun `verify success action gets emitted after task is added`() {
        val successAction = TaskSheetAction.SaveTaskSuccess

        given {
            databaseManager.addTask(any())
        } willReturn {
            Single.just(0L)
        }

        val testObservable = taskViewModel.sheetAction().test()
        taskViewModel.addTask(mockTask)
        testObservable
            .assertNoErrors()
            .assertNotComplete()
            .assertValue {
                it == successAction
            }
    }

    @Test
    fun `verify success action gets emitted after task is updated`() {
        val successAction = TaskSheetAction.SaveTaskSuccess

        given {
            databaseManager.updateTask(any())
        } willReturn {
            Single.just(0)
        }

        val testObservable = taskViewModel.sheetAction().test()
        taskViewModel.updateTask(mockTask)
        testObservable
            .assertNoErrors()
            .assertNotComplete()
            .assertValue {
                it == successAction
            }
    }

    private val mockTask = Task(0, 0, "1", "2", false, Priority.LOW)
}