package com.example.jobintentservice.download

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.jobintentservice.BuildConfig
import okhttp3.internal.Internal.instance
import java.io.File

class DownloadAndStartApk {


    companion object {
        private var instance: DownloadAndStartApk? = null

        @JvmStatic
        fun getInstance(): DownloadAndStartApk =
            instance ?: synchronized(this) {
                instance ?: DownloadAndStartApk().also { instance = it }
            }
    }

    fun getIntent(context: Context): Intent {
        val file =
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                RetrofitClient.FILE_URL1
            )

        val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }

        val openFileIntent = Intent(Intent.ACTION_VIEW)
        openFileIntent.apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            setDataAndType(apkUri, "application/vnd.android.package-archive")
        }
        return openFileIntent
    }
}