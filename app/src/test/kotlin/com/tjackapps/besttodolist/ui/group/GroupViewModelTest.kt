package com.tjackapps.besttodolist.ui.group

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import com.tjackapps.besttodolist.RxSchedulerRule
import com.tjackapps.data.manager.DatabaseManager
import com.tjackapps.data.model.Group
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test

class GroupViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    private val databaseManager: DatabaseManager = mock()
    private val groupViewModel = GroupViewModel(databaseManager)

    @Test
    fun `verify content state gets emitted after groups are loaded`() {
        val contentState = GroupPageState.Content(emptyList())

        given {
            databaseManager.getGroups()
        } willReturn {
            Single.just(emptyList())
        }

        groupViewModel.getGroups()
        groupViewModel.fragmentState()
            .test()
            .assertNoErrors()
            .assertNotComplete()
            .assertValue {
                it == contentState
            }
    }

    @Test
    fun `verify content state gets emitted after group is deleted`() {
        val contentState = GroupPageState.Content(emptyList())

        given {
            databaseManager.deleteTasksFromGroup(any())
        } willReturn {
            Single.just(0)
        }

        given {
            databaseManager.deleteGroup(any())
        } willReturn {
            Single.just(0)
        }

        given {
            databaseManager.getGroups()
        } willReturn {
            Single.just(emptyList())
        }

        groupViewModel.deleteGroup(mockGroup)
        groupViewModel.fragmentState()
            .test()
            .assertNoErrors()
            .assertNotComplete()
            .assertValue {
                it == contentState
            }
    }

    @Test
    fun `verify success action gets emitted after group is added`() {
        val successAction = GroupSheetAction.SaveGroupSuccess

        given {
            databaseManager.addGroup(any())
        } willReturn {
            Single.just(0L)
        }

        val testObservable = groupViewModel.sheetAction().test()
        groupViewModel.addGroup(mockGroup)
        testObservable
            .assertNoErrors()
            .assertNotComplete()
            .assertValue {
                it == successAction
            }
    }

    @Test
    fun `verify success action gets emitted after group is updated`() {
        val successAction = GroupSheetAction.SaveGroupSuccess

        given {
            databaseManager.updateGroup(any())
        } willReturn {
            Single.just(0)
        }

        val testObservable = groupViewModel.sheetAction().test()
        groupViewModel.updateGroup(mockGroup)
        testObservable
            .assertNoErrors()
            .assertNotComplete()
            .assertValue {
                it == successAction
            }
    }

    private val mockGroup = Group(0, "1", "2")
}