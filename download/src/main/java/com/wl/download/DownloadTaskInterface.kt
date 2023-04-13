package com.wl.download

import com.wl.download.retrofit.downloadError
import com.wl.download.retrofit.downloadProcess
import com.wl.download.retrofit.downloadSuccess

/**
 * User: wanglei
 * Date: 2023/4/12 16:34
 * Description:
 */
interface DownloadTaskInterface {
    fun pauseDownload()

    fun reStart()

    suspend fun download(
        url: String, outputFile: String,
        onError: downloadError = {},
        onProcess: downloadProcess = { _, _, _ -> },
        onSuccess: downloadSuccess = { },
        restart: Boolean = false
    )

    fun getDownloadUrl() :String
}