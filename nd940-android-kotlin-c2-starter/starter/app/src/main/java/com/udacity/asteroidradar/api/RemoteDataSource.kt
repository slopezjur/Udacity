package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.ImageOfTheDay
import org.json.JSONObject

class RemoteDataSource {

    private val emptyImageOfTheDay = ImageOfTheDay("", "", "")

    suspend fun getImageOfTheDay(): ImageOfTheDay {
        return NasaApiServiceImpl.retrofitMoshiService.getImageOfTheDay(
            Constants.API_KEY
        ).run {
            if (isSuccessful) {
                // TODO : manage result
                body() ?: emptyImageOfTheDay
            } else {
                emptyImageOfTheDay
            }
        }
    }

    suspend fun getAsteroidsByRange(startDate: String, endDate: String): List<Asteroid> {
        val response = NasaApiServiceImpl.retrofitService.getAsteroidsByDateRangeAsync(
            startDate = startDate,
            endDate = endDate,
            Constants.API_KEY
        ).run {
            if (isSuccessful) {
                body()?.let { body -> parseAsteroidsJsonResult(JSONObject(body)) } ?: emptyList()
            } else {
                emptyList()
            }
        }

        return response
    }
}
