package com.tjackapps.besttodolist.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tjackapps.besttodolist.pages.group
import com.tjackapps.besttodolist.ui.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupEspressoTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun can_create_new_group() {
        group {
            check {
                createGroupButtonIsDisplayed()
            }
            openGroupSheet {
                check {
                    saveButtonIsDisplayed()
                }
                name("Test List")
                description("Just a test")
                save()
            }
        }
    }

    @Test
    fun missing_fields_shows_alert_when_saving_group() {
        group {
            check {
                createGroupButtonIsDisplayed()
            }
            openGroupSheet {
                check {
                    saveButtonIsDisplayed()
                }
                name("Test List")
                save()

                check {
                    errorDialogIsDisplayed()
                }
                closeDialog()
            }
        }
    }
}