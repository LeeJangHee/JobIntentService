package com.example.jobintentservice.download

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.jobintentservice.BuildConfig
import com.example.jobintentservice.PROGRESS_UPDATE
import com.example.jobintentservice.TAG
import com.example.jobintentservice.download.RetrofitClient.FILE_URL
import com.example.jobintentservice.download.RetrofitClient.FILE_URL1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.ResponseBody
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ExampleJobIntentService : JobIntentService() {
    val NOTIFICATION_CHANNEL_ID = "download"
    val NOTIFICATION_ID = 123
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager

    companion object {
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, ExampleJobIntentService::class.java, 123, work)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onHandleWork(intent: Intent) {
        val input = intent.getStringExtra("inputExtra")

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "파일 다운로드",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.apply {
                description = "description"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("다운로드 중")
            .setContentText("다운로드입니다.")
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())


        val request =
            RetrofitClient.getInstance().create(RetrofitService::class.java).downloadFile(FILE_URL1)

        try {
            CoroutineScope(Dispatchers.IO).async {
                request.execute().body()?.let { downloadFile(it) }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun downloadFile(body: ResponseBody) {
        var count: Int
        val data = ByteArray(1024 * 4) { 0 }
        val fileSize = body.contentLength()
        val inputStream = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            FILE_URL1)

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
        notificationBuilder.let {
            it.apply {
                setProgress(100, currentProgress, false)
                setContentText("$currentProgress%")
            }
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun onDownLoadComplete(downloadComplete: Boolean) {
        sendProgressUpdate(downloadComplete)
        val message: String = if (downloadComplete) {
            "다운로드가 완료되었습니다."
        } else {
            "다운로드에 실패하였습니다."
        }

        val intentFun = DownloadAndStartApk.getInstance().getIntent(this)

        val notificationPendingIntent = PendingIntent.getActivity(
            this,
            1234,
            intentFun,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        notificationManager.cancel(NOTIFICATION_ID)
        notificationBuilder.setProgress(0, 0, false)
        notificationBuilder.setContentTitle("다운로드 완료")
        notificationBuilder.setContentText(message)
        notificationBuilder.setContentIntent(notificationPendingIntent)
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun sendProgressUpdate(downloadComplete: Boolean) {
        val intent = Intent(PROGRESS_UPDATE)
        intent.putExtra("downloadComplete", downloadComplete)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onStopCurrentWork(): Boolean {
        notificationManager.cancel(NOTIFICATION_ID)
        return super.onStopCurrentWork()
    }
}