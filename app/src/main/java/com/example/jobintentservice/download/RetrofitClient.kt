package com.example.jobintentservice.download

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var instance: Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()


    const val BASE_URL = "http://wkdgml96.iptime.org:8080/"
    const val FILE_URL = "image/app-debug.apk"

    const val BASE_URL4 = "https://youtu.be/"
    const val FILE_URL4 = "B4gFbWnNpac"


    fun getInstance(): Retrofit {
        if (instance == null) {
            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return instance!!
    }
}
