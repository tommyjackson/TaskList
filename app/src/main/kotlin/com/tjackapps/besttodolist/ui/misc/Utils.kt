package com.tjackapps.besttodolist.ui.misc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Provider

fun View.getLayoutInflater() = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(provider: Provider<T>): T {
    return ViewModelProvider(this,
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return provider.get() as T
        }
    }
    ).get(T::class.java)
}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

infix fun ViewGroup.displayedChild(view: View?) {
    for (index in 0 until childCount) {
        val child = getChildAt(index)
        child.isVisible = child == view
    }
}