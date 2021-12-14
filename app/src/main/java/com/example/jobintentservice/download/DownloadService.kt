package com.example.jobintentservice.download

import android.R
import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.jobintentservice.PROGRESS_UPDATE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.ResponseBody
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class DownloadService() : IntentService("downloadService") {

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null

    override fun onHandleIntent(intent: Intent?) {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("download", "파일 다운로드", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.apply {
                description = "description"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(false)
            }
            notificationManager?.createNotificationChannel(notificationChannel)
        }

        notificationBuilder = NotificationCompat.Builder(this, "download")
            .setSmallIcon(R.drawable.stat_sys_download)
            .setContentText("다운로드입니다")
            .setContentTitle("다운로드중")
            .setDefaults(0)
            .setAutoCancel(true)
        notificationManager?.notify(0, notificationBuilder?.build())

        val request = RetrofitClient.getInstance().create(RetrofitService::class.java).downloadFile(FILE_URL)

        try {
            CoroutineScope(Dispatchers.IO).async {
                request.execute().body()?.let { downloadFile(it) }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun downloadFile(body: ResponseBody) {
        var count: Int
        val data = ByteArray(1024 * 4) { 0 }
        val fileSize = body.contentLength()
        val inputStream = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path,
            FILE_URL1
        )

        if (outputFile.exists()) {
            outputFile.delete()
        }

        val outputStream = FileOutputStream(outputFile)

        var total: Long = 0
        var downloadComplete = false

        count = inputStream.read(data)
        while (count != -1) {
            total += count
            val progress = ((total * 100).toDouble() / fileSize.toDouble()).toInt()

            updateNotification(progress)
            outputStream.write(data, 0, count)
            downloadComplete = true
            count = inputStream.read(data)
        }

        onDownLoadComplete(downloadComplete)
        outputStream.apply {
            flush()
            close()
        }
        inputStream.close()
    }

    fun updateNotification(currentProgress: Int) {
        notificationBuilder?.let {
            it.apply {
                setProgress(100, currentProgress, false)
                setContentText("$currentProgress%")
            }
        }
        notificationManager?.notify(0, notificationBuilder!!.build())
    }

    fun sendProgressUpdate(downloadComplete: Boolean) {
        val intent = Intent(PROGRESS_UPDATE)
        intent.putExtra("downloadComplete", downloadComplete)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    fun onDownLoadComplete(downloadComplete: Boolean) {
        sendProgressUpdate(downloadComplete)
        val message: String = if (downloadComplete) {
            "다운로드가 완료되었습니다."
        } else {
            "다운로드에 실패하였습니다."
        }

        notificationManager?.cancel(0)
        notificationBuilder?.setProgress(0, 0, false)
        notificationBuilder?.setContentText(message)
        notificationBuilder?.setSmallIcon(R.drawable.stat_sys_download_done)
        notificationManager?.notify(0, notificationBuilder?.build())
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
//        super.onTaskRemoved(rootIntent)
        notificationManager?.cancel(0)
    }
}
