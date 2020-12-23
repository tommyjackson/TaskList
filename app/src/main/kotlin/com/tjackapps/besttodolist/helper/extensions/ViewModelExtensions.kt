package com.tjackapps.besttodolist.helper.extensions

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Provider

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