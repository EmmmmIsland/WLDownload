package com.wl.download

import kotlinx.coroutines.Job

/**
 * User: wanglei
 * Date: 2023/4/10 14:16
 * Description:
 */
interface Disposable {
    val isDisposed: Boolean

    fun dispose()
}



internal class BaseTargetDisposable(private val job: Job) : Disposable {

    override val isDisposed
        get() = !job.isActive

    override fun dispose() {
        if (isDisposed) return
        job.cancel()
    }
}