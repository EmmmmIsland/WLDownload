package com.wl.wldownload

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo.State.*
import androidx.work.WorkManager
import com.wl.download.DownloadManager
import com.wl.download.DownloadTask
import com.wl.download.UrlException
import com.wl.download.retrofit.HttpDownload
import com.wl.download.workmanager.DownloadWorker
import com.wl.wldownload.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {
    private val TAG = "WLOK_MainActivity"
    val downloadUrl1 = "https://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4"
    val downloadUrl2 = "https://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4"
    val downloadUrl3 =
        "https://dldir1.qq.com/wework/work_weixin/wxwork_android_3.0.31.13637_100001.apk"
    private lateinit var downloadTask: DownloadTask


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        checkPermission()
        mBinding.tvDown.setOnClickListener {
//            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
//                HttpDownload.download(
//                    downloadUrl1,
//                    File(Environment.getExternalStorageDirectory(), "koko.mp4").toString(),
//                    onError = { Log.d(TAG, "onerror") },
//                    onProcess = { _, _, process ->
//                        Log.d(TAG, "progress" + (process * 100).toInt())
//                        mBinding.tvDown.text = "progress${(process * 100).toInt()}"
//                    },
//                    onSuccess = { Log.d(TAG, "onSuccess") })
//            }

            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                HttpDownload.instance.download(
                    arrayOf(downloadUrl1, downloadUrl2),
                    onError = { Log.d(TAG, "onerror") },
                    onProcess = { _, _, process ->
                        Log.d(TAG, "progress" + (process * 100).toInt())
                        mBinding.tvDown.text = "progress${(process * 100).toInt()}"
                    },
                    onSuccess = { Log.d(TAG, "onSuccess") })
            }
        }

        mBinding.tvWork.setOnClickListener {
            val startDownload = DownloadWorker.startDownload(
                this@MainActivity,
                downloadUrl2,
                this@MainActivity.cacheDir.path,
                "abc.mp4"
            )
            onWorkDownProcess(mBinding.tvDown, startDownload)
        }


        mBinding.tvSSingleDownload.setOnClickListener {
            lifecycleScope.launch {
                HttpDownload.instance.download(
                    downloadUrl1, this@MainActivity.cacheDir.path + "/cccc.mp4"
                )
            }
        }

        mBinding.tvSSinglePause.setOnClickListener {
            HttpDownload.instance.pauseDownload()
        }

        mBinding.tvSSingleCon.setOnClickListener {
            lifecycleScope.launch {
                HttpDownload.instance.downloadCon(
                    downloadUrl1,
                    this@MainActivity.cacheDir.path + "/cccc.mp4"
                )
            }
        }

        mBinding.tvNewDownload.setOnClickListener {
//            lifecycleScope.launch {
//                downloadTask = DownloadTask()
//                downloadTask.download(
//                    downloadUrl1,
//                    this@MainActivity.cacheDir.path + "/dddd.mp4"
//                )
//            }
//            DownloadManager.instance.startDownload(downloadUrl1)
//            DownloadManager.instance.startDownload(downloadUrl2)
            try {
//                DownloadManager.instance.startDownload("23213")
                DownloadManager.instance.startDownload(
                    url = downloadUrl1,
                    fileName = "aaa11.mp4",
                    onError = {it-> Log.d(TAG, "onerror${it}") },
                    onProcess = { _, _, process -> Log.d(TAG, process.toString()) },
                    onSuccess = { Log.d(TAG, "onSuccess") })

                DownloadManager.instance.startDownload(
                    url = downloadUrl2,
                    fileName = "aaa22.mp4",
                    onError = {it-> Log.d(TAG, "onerror${it}") },
                    onProcess = { _, _, process -> Log.d(TAG, process.toString()) },
                    onSuccess = { Log.d(TAG, "onSuccess") })

            } catch (e: UrlException) {
                Toast.makeText(this, "url 格式异常", Toast.LENGTH_SHORT).show()
            }
        }

        mBinding.tvSNewPause1.setOnClickListener {
            DownloadManager.instance.pauseDownload(downloadUrl1)
        }

        mBinding.tvSNewPause2.setOnClickListener {
            DownloadManager.instance.pauseDownload(downloadUrl2)
        }

        mBinding.tvNewCon1.setOnClickListener {
            DownloadManager.instance.restart(downloadUrl1)
        }

        mBinding.tvNewCon2.setOnClickListener {
            DownloadManager.instance.restart(downloadUrl2)
        }
    }

    private fun onWorkDownProcess(tv_msg: TextView, startDownload: UUID) {
        tv_msg.text = ""
        WorkManager.getInstance(applicationContext)
            .getWorkInfoByIdLiveData(startDownload)
            .observe(this) { t -> // 任务执行完毕之后，会在这里获取到返回的结果
                when (t?.state) {
                    RUNNING -> {
//                        tv_msg.append("下载进度：progress =${t.progress.getInt("progress", 0)}% \n")
                        tv_msg.setText("下载进度：progress =${t.progress.getInt("progress", 0)}% \n")
                    }
                    SUCCEEDED -> {
                        tv_msg.append("下载成功\n")
                    }
                    FAILED -> {
                        tv_msg.append("下载失败\n")
                    }
                    else -> {}
                }
            }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf<String>(
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, permissions, 200)
                    return
                }
            }
        }
    }


}