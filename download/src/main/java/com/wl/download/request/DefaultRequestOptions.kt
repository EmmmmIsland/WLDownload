package com.wl.download.request

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * User: wanglei
 * Date: 2023/4/4 17:02
 * Description:
 */
class DefaultRequestOptions(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun copy(
        dispatcher: CoroutineDispatcher = this.dispatcher,
    ) = DefaultRequestOptions(dispatcher)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is DefaultRequestOptions &&
                dispatcher == other.dispatcher
    }

    override fun hashCode(): Int {
        var result = dispatcher.hashCode()
        return result
    }

    companion object {
        @JvmField
        val INSTANCE = DefaultRequestOptions()
    }
}