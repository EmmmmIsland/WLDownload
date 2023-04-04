package com.wl.download.request

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import okhttp3.HttpUrl
import java.io.File

/**
 * User: wanglei
 * Date: 2023/3/30 15:54
 * Description:
 */
class DownLoadRequest private constructor(
    val context: Context,
    val data: Any,
    val defaults: DefaultRequestOptions
) {

    @JvmOverloads
    fun newBuilder(context: Context = this.context) = Builder(this, context)

    class Builder {

        private val context: Context
        private var defaults: DefaultRequestOptions
        private var data: Any?


        constructor(context: Context) {
            this.context = context
            defaults = DefaultRequestOptions.INSTANCE
            data = null

        }

        @JvmOverloads
        constructor(request: DownLoadRequest, context: Context = request.context) {
            this.context = context
            defaults = request.defaults
            data = request.data
        }


        fun data(data: Any?) = apply {
            this.data = data
        }

        fun defaults(defaults: DefaultRequestOptions) = apply {
            this.defaults = defaults
        }


        fun build(): DownLoadRequest {
            return DownLoadRequest(
                context = context,
                data = data ?: NullRequestData,
                defaults = defaults,
            )
        }

    }
}