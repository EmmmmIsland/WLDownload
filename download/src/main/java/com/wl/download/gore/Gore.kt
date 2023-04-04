package com.wl.download.gore

import android.content.Context
import com.wl.download.request.DownloadLoader

/**
 * User: wanglei
 * Date: 2023/3/30 16:17
 * Description:GetMore
 */
object Gore {
    private var downloadLoader: DownloadLoader? = null

    @JvmStatic
    fun downloadLoader(context: Context): DownloadLoader = downloadLoader ?: newDownLoader(context)

    @Synchronized
    private fun newDownLoader(context: Context): DownloadLoader {
        // Check again in case imageLoader was just set.
        downloadLoader?.let { return it }

        // Create a new ImageLoader.
        val newDownLoader = DownloadLoader(context)
        downloadLoader = newDownLoader
        return newDownLoader
    }
}