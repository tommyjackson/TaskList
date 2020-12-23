package com.tjackapps.besttodolist.ui.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tjackapps.besttodolist.R

class GroupSheetPage {

    private val name = withId(R.id.name_entry)
    private val description = withId(R.id.description_entry)
    private val saveButton = withId(R.id.save_button)
    private val errorDialog = withText("OK")

    fun check(func: Assertions.() -> Unit): Assertions {
        return Assertions().apply(func)
    }

    fun name(text: String) {
        onView(name).perform(typeText(text))
    }

    fun description(text: String) {
        onView(description).perform(typeText(text))
    }

    fun save() {
        onView(saveButton).perform(click())
    }

    fun closeDialog() {
        onView(errorDialog).perform(click())
    }

    inner class Assertions {
        fun saveButtonIsDisplayed() {
            onView(saveButton).check(matches(isCompletelyDisplayed()))
        }

        fun errorDialogIsDisplayed() {
            onView(errorDialog).check(matches(isCompletelyDisplayed()))
        }
    }
}