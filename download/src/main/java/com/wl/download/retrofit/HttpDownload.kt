package com.wl.download.retrofit

import android.os.Environment
import android.util.Log
import com.wl.download.retrofit.HttpKit.apiService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream

object HttpDownload {

    @JvmOverloads
    suspend fun download(
        urlArray: Array<String>, outputFile: String,
        onError: downloadError = {},
        onProcess: downloadProcess = { _, _, _ -> },
        onSuccess: downloadSuccess = {}
    ) {
//        for (url in urlArray) {
//            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
//                download(url, outputFile+ url[0], onProcess = { _, _, process ->
//                    Log.d("WLOK_KKKKKK", "${url}:${process}")
//                })
//            }
//        }

//        for (url in urlArray) {
//            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
//                download(url, outputFile+ url[0], onProcess = { _, _, process ->
//                    Log.d("WLOK_KKKKKK", "${url}:${process}")
//                })
//            }
//        }


        for (i in urlArray.indices) {
            val toString1 = File(Environment.getExternalStorageDirectory(), "koko1.mp4").toString()
            val toString2 = File(Environment.getExternalStorageDirectory(), "koko2.mp4").toString()
            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                download(urlArray[i], if (i==0) toString1 else toString2 , onProcess = { _, _, process ->
                    Log.d("WLOK_KKKKKK", "${urlArray[i]}:${process}")
                })
            }
        }
    }

    @JvmOverloads
    suspend fun download(
        url: String, outputFile: String,
        onError: downloadError = {},
        onProcess: downloadProcess = { _, _, _ -> },
        onSuccess: downloadSuccess = { }
    ) {
        flow {
            try {
                val body = apiService.downloadFile(url)
                val contentLength = body.contentLength()
                val inputStream = body.byteStream()
                val file = File(outputFile)
                val outputStream = FileOutputStream(file)
                var currentLength = 0
                val bufferSize = 1024 * 8
                val buffer = ByteArray(bufferSize)
                val bufferedInputStream = BufferedInputStream(inputStream, bufferSize)
                var readLength: Int
                while (bufferedInputStream.read(buffer, 0, bufferSize)
                        .also { readLength = it } != -1
                ) {
                    outputStream.write(buffer, 0, readLength)
                    currentLength += readLength
                    emit(
                        HttpResult.progress(
                            currentLength.toLong(),
                            contentLength,
                            currentLength.toFloat() / contentLength.toFloat()
                        )
                    )
                }
                bufferedInputStream.close()
                outputStream.close()
                inputStream.close()
                emit(HttpResult.success(file))
            } catch (e: Exception) {
                emit(HttpResult.failure<File>(e))
            }
        }.flowOn(Dispatchers.IO)
            .collect {
                it.fold(onFailure = { e ->
                    e?.let { it1 -> onError(it1) }
                }, onSuccess = { file ->
                    onSuccess(file)
                }, onLoading = { progress ->
                    onProcess(progress.currentLength, progress.length, progress.process)
                })
            }
    }

//    @JvmOverloads
//    suspend fun download(
//        url: String, outputFile: String,
//        onError: downloadError = {},
//        onProcess: downloadProcess = { _, _, _ -> },
//        onSuccess: downloadSuccess = { }
//    )  =
//        withContext(Dispatchers.IO){
//            flow {
//                try {
//                    val body = apiService.downloadFile(url)
//                    val contentLength = body.contentLength()
//                    val inputStream = body.byteStream()
//                    val file = File(outputFile)
//                    val outputStream = FileOutputStream(file)
//                    var currentLength = 0
//                    val bufferSize = 1024 * 8
//                    val buffer = ByteArray(bufferSize)
//                    val bufferedInputStream = BufferedInputStream(inputStream, bufferSize)
//                    var readLength: Int
//                    while (bufferedInputStream.read(buffer, 0, bufferSize)
//                            .also { readLength = it } != -1
//                    ) {
//                        outputStream.write(buffer, 0, readLength)
//                        currentLength += readLength
//                        emit(
//                            HttpResult.progress(
//                                currentLength.toLong(),
//                                contentLength,
//                                currentLength.toFloat() / contentLength.toFloat()
//                            )
//                        )
//                    }
//                    bufferedInputStream.close()
//                    outputStream.close()
//                    inputStream.close()
//                    emit(HttpResult.success(file))
//                } catch (e: Exception) {
//                    emit(HttpResult.failure<File>(e))
//                }
//            }.flowOn(Dispatchers.IO)
//                .collect {
//                    it.fold(onFailure = { e ->
//                        e?.let { it1 -> onError(it1) }
//                    }, onSuccess = { file ->
//                        onSuccess(file)
//                    }, onLoading = { progress ->
//                        onProcess(progress.currentLength, progress.length, progress.process)
//                    })
//                }
//        }
}
