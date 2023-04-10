package com.wl.download.retrofit

import android.os.Environment
import android.util.Log
import com.wl.download.data.DownloadModel
import com.wl.download.retrofit.HttpKit.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream


class HttpDownload private constructor() {
    companion object {
        val instance = SingletonHolder.holder
        val downloadModelList = arrayListOf<DownloadModel>()
    }

    private object SingletonHolder {
        val holder = HttpDownload()
    }

    @JvmOverloads
    suspend fun download(
        urlArray: Array<String>,
        outputDirectory: File = Environment.getExternalStorageDirectory(),
        onError: downloadError = {},
        onProcess: downloadProcess = { _, _, _ -> },
        onSuccess: downloadSuccess = {}
    ) {

        for (i in urlArray.indices) {
            var fileName = urlArray[i].substring(urlArray[i].lastIndexOf("/").plus(1))
            if (fileName.isEmpty()) fileName = "${System.currentTimeMillis()}.mp4"
            val pathName = fileName
            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                download(urlArray[i], pathName,
                    onSuccess = {},
                    onError = {},
                    onProcess = { _, _, process ->
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

}
