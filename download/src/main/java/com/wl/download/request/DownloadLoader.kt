package com.wl.download.request

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import com.wl.download.RealDownloadLoader
import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.File

/**
 * User: wanglei
 * Date: 2023/3/30 15:47
 * Description:
 */
interface DownloadLoader {
    fun enqueue(request: DownLoadRequest): Disposable

    class Builder(context: Context) {

        private val applicationContext = context.applicationContext

        fun build(): DownloadLoader {

            return RealDownloadLoader(
                applicationContext = applicationContext
            )
        }

    }

    companion object {
        @JvmStatic
        @JvmName("create")
        operator fun invoke(context: Context) = Builder(context).build()
    }
}