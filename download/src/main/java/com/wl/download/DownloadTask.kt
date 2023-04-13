package com.wl.download

import android.util.Log
import com.wl.download.retrofit.*
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
import java.io.RandomAccessFile

/**
 * User: wanglei
 * Date: 2023/4/12 13:41
 * Description:
 */
class DownloadTask : DownloadTaskInterface {
    private val TAG = "WLOK-DownloadTask"
    var isPause = false
    var downloadLength = 0L
    var fileLength = 0
    var url: String = ""
    var outputFile: String = ""

    override fun pauseDownload() {
        isPause = true
    }

    override fun reStart() {
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            download(url = url, outputFile = outputFile, restart = true)
        }
    }

    override fun getDownloadUrl(): String {
        return url
    }

    @JvmOverloads
    override suspend fun download(
        url: String, outputFile: String,
        onError: downloadError,
        onProcess: downloadProcess,
        onSuccess: downloadSuccess,
        restart: Boolean
    ) {
        this.url = url
        this.outputFile = outputFile
        if (restart) isPause = false
        flow {
            try {
                val body = if (restart) {
                    apiService.downloadFile(url, "bytes=${downloadLength}-${fileLength}")
                } else {
                    apiService.downloadFile(url)
                }
                val contentLength = body.contentLength()
                fileLength = contentLength.toInt()
                val inputStream = body.byteStream()
                val file = File(outputFile)
                val randomAccess = RandomAccessFile(file, "rwd")
                if (restart) randomAccess.seek(downloadLength)
                var currentLength = if (restart) downloadLength else 0L
                val bufferSize = 1024 * 8
                val buffer = ByteArray(bufferSize)
                val bufferedInputStream = BufferedInputStream(inputStream, bufferSize)
                var readLength: Int
                while (bufferedInputStream.read(buffer, 0, bufferSize)
                        .also { readLength = it } != -1 && !isPause
                ) {
                    randomAccess.write(buffer, 0, readLength)
                    currentLength += readLength
                    emit(
                        HttpResult.progress(
                            currentLength.toLong(),
                            contentLength,
                            currentLength.toFloat() / contentLength.toFloat()
                        )
                    )
                    downloadLength = currentLength
                    Log.i(TAG, " task length${downloadLength}--${contentLength}")
                }
                bufferedInputStream.close()
                randomAccess.close()
                inputStream.close()
                emit(HttpResult.success(file))
            } catch (e: Exception) {
                emit(HttpResult.failure<File>(e))
            }
        }.flowOn(Dispatchers.IO)
            .collect {
                it.fold(onFailure = { e ->
                    Log.i(TAG, "onFailure:$e")
                    e?.let { it1 -> onError(it1) }
                }, onSuccess = { file ->
                    Log.i(TAG, "onSuccess:$file")
                    onSuccess(file)
                }, onLoading = { progress ->
                    Log.i(TAG, "onLoading:$progress")
                    onProcess(progress.currentLength, progress.length, progress.process)
                })
            }
    }
}