package com.wl.download

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.wl.download.retrofit.downloadError
import com.wl.download.retrofit.downloadProcess
import com.wl.download.retrofit.downloadSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.w3c.dom.Text


class DownloadManager private constructor() {
    private val TAG = "WLOK-DownloadManager"
    private val downloadTaskList: ArrayList<DownloadTaskInterface> =
        arrayListOf<DownloadTaskInterface>()

    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = DownloadManager()
    }

    fun startDownload(
        url: String,
        fileDir: String = Environment.getExternalStorageDirectory().toString(),
        fileName: String = defaultName(url),
        onError: downloadError = {},
        onProcess: downloadProcess = { _, _, _ -> },
        onSuccess: downloadSuccess = {},
    ) {
        if (isDownloading(url)) return
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            val taskInterface: DownloadTaskInterface = DownloadTask()
            downloadTaskList.add(taskInterface)
            taskInterface.download(
                url,
                "$fileDir/$fileName",
                onSuccess = {success ->
                    removeTask(url)
                    onSuccess(success)},
                onProcess = onProcess,
                onError = { error ->
                    removeTask(url)
                    onError(error)
                }
            )
        }
    }

    private fun removeTask(url: String): Boolean {
        return downloadTaskList.removeIf {
            TextUtils.equals(it.getDownloadUrl(), url)
        }.apply {
            Log.i(TAG, "removeTask:$url")
        }
    }

    private fun isDownloading(url: String): Boolean {
        for (task in downloadTaskList) {
            if (task!=null && TextUtils.equals(task.getDownloadUrl(), url)) {
                return true
            }
        }
        return false
    }

    private fun defaultName(url: String): String {
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            throw UrlException()
        }
        return if (url.isEmpty()) {
            "${System.currentTimeMillis()}.${url.substring(url.lastIndexOf(".").plus(1))}"
        } else {
            url.substring(url.lastIndexOf("/").plus(1))
        }
    }

    fun pauseDownload(url: String): Boolean {
        for (task in downloadTaskList) {
            if (TextUtils.equals(task.getDownloadUrl(), url)) {
                task.pauseDownload()
                return true
            }
        }
        return false
    }

    fun pauseAll() {
        for (task in downloadTaskList) {
            task.pauseDownload()
        }
    }

    fun restart(url: String){
        for (task in downloadTaskList) {
            if (TextUtils.equals(task.getDownloadUrl(), url)) {
                task.reStart()
            }
        }
    }
}
