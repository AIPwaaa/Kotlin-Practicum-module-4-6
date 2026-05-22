package com.example.myapplication.data.remote

import com.example.myapplication.data.remote.dto.PhotoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PicsumApi {
    @GET("v2/list")
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<PhotoDto>

    companion object {
        const val BASE_URL = "https://picsum.photos/"
    }
}
