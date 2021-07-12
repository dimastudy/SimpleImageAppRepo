package com.example.simplephotoapp.network

import com.example.simplephotoapp.BuildConfig

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query





interface UnsplashApiService {

    companion object {
        private const val API_KEY = BuildConfig.UNSPLASH_API_KEY

        const val BASE_URL = "https://api.unsplash.com/"
    }

    @Headers("Authorization: Client-ID $API_KEY")
    @GET("search/photos")
    suspend fun getPhotoResult(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ):  PhotosNetwork
}