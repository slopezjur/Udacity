package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.ImageOfTheDay
import kotlinx.coroutines.Deferred
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {

    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidsByDateRangeAsync(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String,
    ): Response<String>

    @GET("planetary/apod")
    suspend fun getImageOfTheDay(
        @Query("api_key") apiKey: String
    ): Response<ImageOfTheDay>
}
