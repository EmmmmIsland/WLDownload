package com.wl.download.data

/**
 * User: wanglei
 * Date: 2023/4/10 16:57
 * Description:
 */
data class DownloadModel(
    val url: String,
    val length: Long,
    val finished: Boolean,
    val isStop: Boolean
)

