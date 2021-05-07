package com.example.jobintentservice.download

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface RetrofitService {

    @GET
    @Streaming
    fun downloadFile(@Url url:String): Call<ResponseBody>
}