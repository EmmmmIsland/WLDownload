package com.wl.download.request

/**
 * User: wanglei
 * Date: 2023/3/30 14:22
 * Description:
 */
interface Disposable {

    /**
     * Returns true if the request is complete or cancelling.
     */
    val isDisposed: Boolean

    /**
     * Cancels any in progress work and frees any resources associated with this request. This method is idempotent.
     */
    fun dispose()

    /**
     * Suspends until any in progress work completes.
     */

    suspend fun await()
}