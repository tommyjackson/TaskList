package com.tjackapps.besttodolist.helper.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}