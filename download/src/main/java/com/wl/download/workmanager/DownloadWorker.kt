package com.wl.download.workmanager

import android.content.Context
import android.util.Log
import androidx.work.*
import com.wl.download.retrofit.HttpDownload
import java.util.*


class DownloadWorker(context: Context, parameters: WorkerParameters) :
        CoroutineWorker(context, parameters) {
    private val TAG = "WLOK_DownloadWorker"
    override suspend fun doWork(): Result {
        val inputUrl = inputData.getString(KEY_INPUT_URL)
                ?: return Result.failure()
        val outputUrl = inputData.getString(KEY_OUT_PUT_URL)
                ?: return Result.failure()
        val filename = inputData.getString(KEY_OUTPUT_FILE_NAME)
                ?: return Result.failure()
        return if (download(inputUrl, outputUrl, filename)){
            Log.d(TAG,"doWork success")
            Result.success()
        } else {
            Log.d(TAG,"doWork failure")
            Result.failure()
        }
    }

    private suspend fun download(downloadUrl: String, outputFile: String, fileName: String) :Boolean  {
        var isSuccess = false
        HttpDownload.download(downloadUrl, "$outputFile/$fileName", onProcess = { _, _, process ->
            setProgress(Data.Builder().let {
                it.putInt("progress", (process * 100).toInt())
                it.build()
            })
        }, onSuccess = {
            Log.d(TAG,"onSuccess")
            isSuccess = true
        }, onError = {
            Log.d(TAG,"onError")
            isSuccess = false
            it.printStackTrace()
        })
        Log.d(TAG,"result:${isSuccess}")
        return isSuccess
    }

    companion object {
        const val KEY_INPUT_URL = "KEY_INPUT_URL"
        const val KEY_OUT_PUT_URL = "KEY_OUT_URL"
        const val KEY_OUTPUT_FILE_NAME = "KEY_OUTPUT_FILE_NAME"

        fun startDownload(context: Context, downloadUrl: String, outputFile: String, fileName: String): UUID {
            val inputData: Data = Data.Builder().apply {
                putString(KEY_INPUT_URL, downloadUrl)
                putString(KEY_OUTPUT_FILE_NAME, fileName)
                putString(KEY_OUT_PUT_URL, outputFile)
            }.build()
            val request = OneTimeWorkRequestBuilder<DownloadWorker>().setInputData(inputData).build()
            WorkManager.getInstance(context).enqueue(request)
            return request.id
        }
    }
}