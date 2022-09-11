package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.AsteroidRadarRepository
import com.udacity.asteroidradar.api.NasaApiStatus
import com.udacity.asteroidradar.api.RemoteDataSource
import com.udacity.asteroidradar.database.AsteroidRadarRoom.Companion.getDatabaseInstance
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _nasaApiStatus = MutableLiveData<NasaApiStatus>()
    val nasaApiStatus: LiveData<NasaApiStatus>
        get() = _nasaApiStatus

    // TODO : Hello, Hilt
    private val remoteDataSource = RemoteDataSource()
    private val database = getDatabaseInstance(application)
    private val asteroidRadarRepository = AsteroidRadarRepository(remoteDataSource, database)

    val imageOfTheDay = asteroidRadarRepository.getImageOfTheDay()
    var asteroids = asteroidRadarRepository.getAsteroids()

    init {
        viewModelScope.launch {
            _nasaApiStatus.value = NasaApiStatus.LOADING
            try {
                asteroidRadarRepository.refreshImageOfTheDay()
                asteroidRadarRepository.refreshAsteroids()
                _nasaApiStatus.value = NasaApiStatus.DONE
            } catch (exception: Exception) {
                _nasaApiStatus.value = NasaApiStatus.ERROR
            }
        }
    }

    fun getAsteroidFromLocal() {
        asteroids = asteroidRadarRepository.getAsteroids()
    }

    fun getWeekAsteroidsFromLocal() {
        asteroids = asteroidRadarRepository.getWeekAsteroidsFromLocal()
    }

    fun getTodayAsteroidsFromLocal() {
        asteroids = asteroidRadarRepository.getTodayAsteroidsFromLocal()
    }
}