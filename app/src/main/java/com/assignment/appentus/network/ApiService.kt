package com.assignment.appentus.network

import com.assignment.appentus.Constants
import com.assignment.appentus.pojo.ImageURL
import com.assignment.appentus.pojo.Results
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.HashMap


interface ApiService {
    companion object{


        private val retrofitService by lazy {

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
//                    .client(client)
                .build()
                .create(ApiService::class.java)
        }

        fun getInstance():ApiService= retrofitService
    }
    @GET(Constants.LIST + "?")
     fun getImageList(@Query(Constants.PAGE)pageNumber:Int): Call<List<ImageURL>>

    @GET(Constants.LIST + "?")
    suspend fun getImageList2(@Query(Constants.PAGE)pageNumber:Int): Response<List<ImageURL>>


}