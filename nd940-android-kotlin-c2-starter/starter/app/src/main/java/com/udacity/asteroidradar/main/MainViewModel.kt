package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.AsteroidRadarRepository
import com.udacity.asteroidradar.api.RemoteDataSource
import com.udacity.asteroidradar.database.AsteroidRadarRoom.Companion.getDatabaseInstance
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // TODO : Hello, Hilt
    private val remoteDataSource = RemoteDataSource()
    private val database = getDatabaseInstance(application)
    private val asteroidRadarRepository = AsteroidRadarRepository(remoteDataSource, database)

    val imageOfTheDay = asteroidRadarRepository.getImageOfTheDay()
    val asteroids = asteroidRadarRepository.getAsteroids()

    init {
        viewModelScope.launch {
            asteroidRadarRepository.refreshImageOfTheDay()
        }

        viewModelScope.launch {
            asteroidRadarRepository.refreshAsteroids()
        }
    }
}