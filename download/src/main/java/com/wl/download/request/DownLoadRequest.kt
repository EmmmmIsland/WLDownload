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
    val data: Any
){

    class Builder {

        private val context: Context
        private var data: Any?


        constructor(context: Context) {
            this.context = context
            data = null

        }

        @JvmOverloads
        constructor(request: DownLoadRequest, context: Context = request.context) {
            this.context = context
            data = request.data
        }

        /**
         * Set the data to load.
         *
         * The default supported data types are:
         * - [String] (mapped to a [Uri])
         * - [Uri] ("android.resource", "content", "file", "http", and "https" schemes only)
         * - [HttpUrl]
         * - [File]
         * - [DrawableRes]
         * - [Drawable]
         * - [Bitmap]
         */
        fun data(data: Any?) = apply {
            this.data = data
        }



        /**
         * Create a new [ImageRequest].
         */
        fun build(): DownLoadRequest {
            return DownLoadRequest(
                context = context,
                data = data ?: NullRequestData,
            )
        }

    }
}