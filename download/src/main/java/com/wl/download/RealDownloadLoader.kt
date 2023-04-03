package com.wl.download

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.wl.download.model.DownloadInfo
import com.wl.download.request.Disposable
import com.wl.download.request.DownLoadRequest
import com.wl.download.request.DownloadLoader
import com.wl.download.request.DownloadTargetDisposable
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 * User: wanglei
 * Date: 2023/3/30 15:09
 * Description:
 */
internal class RealDownloadLoader(
    applicationContext: Context
//    data: List<DownloadInfo>?
) : DownloadLoader {
    val TAG: String = "RealDownloadLoader"

//    fun download(
//
//    ): Disposable {
//        Toast.makeText(application, data?.get(0)?.url, Toast.LENGTH_SHORT).show()
//        val disposable = DownloadTargetDisposable()
//        return disposable
//    }


    override fun enqueue(request: DownLoadRequest): Disposable {
        val disposable = DownloadTargetDisposable()
        Toast.makeText(request.context, (request.data as List<DownloadInfo>)[0].url , Toast.LENGTH_SHORT).show()
        okhttpDownload()
        return disposable
    }

    fun okhttpDownload(){
        val size = 1440823L
        val url = "https://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4"
//        "https://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4"
        val saveDir = "koko"
        val request  = Request.Builder().url(url).build()
        val okHttpClient = OkHttpClient()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG", e.message ?: "")
            }

            override fun onResponse(call: Call, response: Response) {
                var inputStream: InputStream? = null
                val buf = ByteArray(2048)
                var len = 0
                var fos: FileOutputStream? = null
                // 储存下载文件的目录
                // 储存下载文件的目录
                val savePath: String? = isExistDir(saveDir)
                try {
                    inputStream = response.body()!!.byteStream()
                    val total = response.body()!!.contentLength()

                    val file = File(savePath, getNameFromUrl(url))
                    fos = FileOutputStream(file)
                    var sum: Long = 0
                    while (inputStream.read(buf).also { len = it } != -1) {
                        fos.write(buf, 0, len)
                        sum += len.toLong()
                        val progress = (sum * 1.0f / total * 100).toInt()
                        // 下载中
//                        listener.onDownloading(progress)
                        Log.d(TAG,"progress:"+progress)

                    }
                    fos.flush()
                    // 下载完成
//                    listener.onDownloadSuccess()
                    Log.d(TAG,"progress:onDownloadSuccess")

                } catch (e: Exception) {
//                    listener.onDownloadFailed()
                } finally {
                    try {
                        inputStream?.close()
                    } catch (e: IOException) {
                    }
                    try {
                        fos?.close()
                    } catch (e: IOException) {
                    }
                }
            }
        })
    }


    @Throws(IOException::class)
    private fun isExistDir(saveDir: String): String? {
        // 下载位置 /storage/emulated/0/
        val downloadFile =
            File(Environment.getExternalStorageDirectory(), saveDir)
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile()
        }
        return downloadFile.absolutePath
    }

    fun getNameFromUrl(url: String): String {
        return url.substring(url.lastIndexOf("/") + 1)
    }
}