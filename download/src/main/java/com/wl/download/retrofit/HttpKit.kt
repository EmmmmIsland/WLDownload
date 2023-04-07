package com.wl.download.retrofit

import retrofit2.Retrofit
import java.io.File


typealias download_error = suspend (Throwable) -> Unit
typealias download_process = suspend (downloadedSize: Long, length: Long, progress: Float) -> Unit
typealias download_success = suspend (uri: File) -> Unit


object HttpKit {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.xx.com")
        .validateEagerly(true) //在开始的时候直接开始检测所有的方法
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

}