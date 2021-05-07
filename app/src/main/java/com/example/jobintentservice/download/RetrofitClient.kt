package com.example.jobintentservice.download

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var instance: Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()

    // http://wkdgml96.iptime.org:8080/image/app-debug.apk
    // https://vc-apl-web.s3.ap-northeast-2.amazonaws.com/apk/download-test-apk.apk
//    const val BASE_URL = "https://vc-apl-web.s3.ap-northeast-2.amazonaws.com"
//    const val FILE_URL = "/apk/download-test-apk.apk"
    const val BASE_URL = "http://wkdgml96.iptime.org:8080/"
    const val FILE_URL = "image/app-debug.apk"

    const val BASE_URL1 = "https://vc-apl-web.s3.ap-northeast-2.amazonaws.com/apk/"
    const val FILE_URL1 = "download-test-apk.apk"

    const val BASE_URL2 = "https://vse-s3demo.voicecaddie.co.kr/webview/app-deploy/AND/"
    const val FILE_URL2 = "com.vcinc.vse-debug-0.9.1.21030803.apk"

    const val BASE_URL3 = "https://vse-s3demo.voicecaddie.co.kr/webview/app-deploy/"
    const val FILE_URL3 = "index.html"

    const val BASE_URL4 = "https://youtu.be/"
    const val FILE_URL4 = "B4gFbWnNpac"


    fun getInstance(): Retrofit {
        if (instance == null) {
            instance = Retrofit.Builder()
                .baseUrl(BASE_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return instance!!
    }
}