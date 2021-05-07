package com.example.jobintentservice.download

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.jobintentservice.TAG


class ExampleJobService : JobService() {

    val JOB_ID = 1000
    private var jobCancelled = false

    override fun onDestroy() {
        super.onDestroy()
        toast("All work complete")
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Job started")
        doBackgroundWork(params)
        return true;
    }
    private fun doBackgroundWork(params: JobParameters?) {
        Thread(Runnable {
            for (i in 0..9) {
                Log.d(TAG, "run: $i")
                if (jobCancelled) {
                    return@Runnable
                }
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            Log.d(TAG, "Job finished")
            jobFinished(params, false)
        }).start()
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Job cancelled before completion")
        jobCancelled = true;
        return true;
    }

    val mHandler: Handler = Handler()

    // Helper for showing tests
    fun toast(text: CharSequence?) {
        mHandler.post(Runnable {
            Toast.makeText(
                this@ExampleJobService,
                text,
                Toast.LENGTH_SHORT
            ).show()
        })
    }
}