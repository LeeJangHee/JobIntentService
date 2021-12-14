package com.example.jobintentservice

import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.jobintentservice.download.DownloadAndStartApk
import com.example.jobintentservice.download.DownloadService
import com.example.jobintentservice.download.ExampleJobIntentService
import com.example.jobintentservice.download.FileType
import kotlinx.android.synthetic.main.main_activity.*
import java.io.File

const val TAG = "janghee"
const val PROGRESS_UPDATE = "progress_update"

class MainActivity : AppCompatActivity() {
    lateinit var view: View
    var bool: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.container, MainFragment())
//            .commitNow()

        view = findViewById<ConstraintLayout>(R.id.rootView)
        download.setOnClickListener {
            requestPermission()
            bool = false
        }

        job_service.setOnClickListener {
//            val intent = Intent(this, ExampleJobIntentService::class.java)
//            intent.putExtra("inputExtra", "abc")
//            ExampleJobIntentService.enqueueWork(this, intent)
            requestPermission()
            bool = true
        }

        registerReceiver()
    }


    fun enqueueWork() {

        val serviceIntent = Intent(this, ExampleJobIntentService::class.java)
        serviceIntent.putExtra("fileType", FileType.VIDEO.toString())
        serviceIntent.putExtra("isNotification", bool)

        ExampleJobIntentService.enqueueWork(this, serviceIntent)
    }

    private val onDownloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == PROGRESS_UPDATE) {
                val complete: Boolean = intent.getBooleanExtra("downloadComplete", false)
                if (complete) {
                    Toast.makeText(this@MainActivity, "completed", Toast.LENGTH_SHORT).show()
                    val intentFun = DownloadAndStartApk.getInstance().getIntent(this@MainActivity)
                    startActivity(intentFun)
//                    val file =
//                        File(
//                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//                            FILE_URL1
//                        )
//
//                    val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        FileProvider.getUriForFile(
//                            context,
//                            BuildConfig.APPLICATION_ID + ".fileprovider",
//                            file
//                        )
//                    } else {
//                        Uri.fromFile(file)
//                    }
//
//                    val openFileIntent = Intent(Intent.ACTION_VIEW)
//                    openFileIntent.apply {
//                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        setDataAndType(apkUri, "application/vnd.android.package-archive")
//                    }
//                    startActivity(openFileIntent)
//                    unregisterReceiver(this)
//                    finish()
                }
            }
        }
    }

    fun registerReceiver() {
        val manager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(PROGRESS_UPDATE)
        manager.registerReceiver(onDownloadReceiver, intentFilter)
    }

    fun startFileDownload() {
        val intent = Intent(this, DownloadService::class.java)
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        val manager = LocalBroadcastManager.getInstance(this)
        manager.unregisterReceiver(onDownloadReceiver)
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enqueueWork()
                } else {
                    Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}
