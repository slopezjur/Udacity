package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.state.FailException
import com.udacity.asteroidradar.state.Resource

class RemoteDataSource(private val nasaApiService: NasaApiServiceImpl) {

    suspend fun getAsteroidsByRange(startDate: String, endDate: String): Resource<List<Asteroid>> {
        return nasaApiService.retrofitService.getAsteroidsByDateRange(
            startDate = startDate,
            endDate = endDate,
            Constants.API_KEY
        ).run {
            if (isSuccessful) {
                body()?.let { body -> Resource.Success(parseAsteroidsJsonResult(body)) } ?: Resource.Failure(
                    FailException.EmptyBody
                )
            } else {
                Resource.Failure(FailException.BadRequest)
            }
        }
    }
}
