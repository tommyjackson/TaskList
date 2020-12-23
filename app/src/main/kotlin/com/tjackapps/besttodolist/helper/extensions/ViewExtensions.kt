package com.tjackapps.besttodolist.helper.extensions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible

fun View.getLayoutInflater() = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

infix fun ViewGroup.displayedChild(view: View?) {
    for (index in 0 until childCount) {
        val child = getChildAt(index)
        child.isVisible = child == view
    }
}