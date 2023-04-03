package com.wl.download.request

/**
 * User: wanglei
 * Date: 2023/3/30 15:15
 * Description:
 */
internal class DownloadTargetDisposable() : Disposable{
    override val isDisposed
        get() = true

    override fun dispose() {
        if (isDisposed) return

    }

    override suspend fun await() {
        if (isDisposed) return

    }
}