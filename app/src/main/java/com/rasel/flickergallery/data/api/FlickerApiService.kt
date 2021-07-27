package com.rasel.flickergallery.data.api

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickerApiService {

    @GET("services/feeds/photos_public.gne?format=json")
    suspend fun getImages(
        @Query("tags") tags: String?,
        @Query("tagmode") tagMode: String = "any"
    ): Response<String>

    companion object {
        private const val BASE_URL: String = "https://www.flickr.com/"

        fun create(client: OkHttpClient): FlickerApiService {
            return Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(FlickerApiService::class.java)
        }
    }
}