package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.state.Resource
import kotlinx.coroutines.flow.flow

class NasaRepositoryImpl constructor(
    private val remoteDataSource: RemoteDataSource,
) {

    fun getAsteroidsByRange(startDate: String, endDate: String) = flow {
        emit(Resource.Loading)
        val asteroids = remoteDataSource.getAsteroidsByRange(startDate, endDate)
        emit(asteroids)
    }
}
