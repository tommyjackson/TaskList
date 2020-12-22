package com.tjackapps.besttodolist.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tjackapps.besttodolist.R

fun group(func: GroupPage.() -> Unit) = GroupPage().apply { func() }

class GroupPage {

    private val createGroupButton = withId(R.id.add_group_button)

    fun check(func: Assertions.() -> Unit): Assertions {
        return Assertions().apply(func)
    }

    fun openGroupSheet(func: GroupSheetPage.() -> Unit): GroupSheetPage {
        onView(createGroupButton).perform(click())
        return GroupSheetPage().apply(func)
    }

    inner class Assertions {

        fun createGroupButtonIsDisplayed() {
            onView(createGroupButton).check(matches(isCompletelyDisplayed()))
        }

    }
}