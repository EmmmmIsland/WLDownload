package com.wl.download

import android.content.Context
import com.wl.download.model.DownloadInfo
import com.wl.download.request.Disposable
import com.wl.download.request.DownLoadRequest
import com.wl.download.request.DownloadLoader
import com.wl.download.request.Gore

/**
 * User: wanglei
 * Date: 2023/3/28 17:28
 * Description:
 */


@JvmSynthetic
inline fun downLoad(
    context: Context,
    downloadLoader: DownloadLoader = Gore.downloadLoader(context),
    url: String?,
    type: Int
): Disposable {
    return downLoadAny(context, downloadLoader, listOf<DownloadInfo>(DownloadInfo(url?:"", type)))
}

@JvmSynthetic
inline fun downLoadAny(
    context: Context,
    downloadLoader: DownloadLoader = Gore.downloadLoader(context),
    data: Any,
): Disposable {
    val downLoadRequest = DownLoadRequest.Builder(context).data(data).build()
    return downloadLoader.enqueue(downLoadRequest)
}