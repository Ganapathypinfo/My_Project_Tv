package com.television.myprojecttv.networkcall

import com.television.myprojecttv.Movie
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RetrofitService {
    @GET("list")
    suspend fun getAllMovies() : Response<List<Movie>>

    companion object {
        var retrofitService: RetrofitService? = null
        fun getInstance() : RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
//                    .baseUrl("https://howtodoandroid.com/")
                    .baseUrl("https://62471195e3450d61b006ae74.mockapi.io/videos/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

    }
}
