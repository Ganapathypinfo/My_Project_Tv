package com.television.myprojecttv

import com.television.myprojecttv.networkcall.RetrofitService

class MainRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getAllMovies() = retrofitService.getAllMovies()

}