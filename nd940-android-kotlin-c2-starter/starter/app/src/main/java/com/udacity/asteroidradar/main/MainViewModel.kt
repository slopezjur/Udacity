package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.ImageOfTheDay
import com.udacity.asteroidradar.api.AsteroidRepository
import com.udacity.asteroidradar.api.RemoteDataSource
import com.udacity.asteroidradar.database.AsteroidRoom.Companion.getInstance
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _imageOfTheDay = MutableLiveData<ImageOfTheDay>()
    val imageOfTheDay: LiveData<ImageOfTheDay>
        get() = _imageOfTheDay

    // TODO : Hello, Hilt
    private val remoteDataSource = RemoteDataSource()
    private val database = getInstance(application)
    private val asteroidRepository = AsteroidRepository(remoteDataSource, database)

    init {
        viewModelScope.launch {
            _imageOfTheDay.value = asteroidRepository.getImageOfTheDay()
        }

        viewModelScope.launch {
            asteroidRepository.getAsteroids()
        }

        _asteroids.value = asteroidRepository.asteroids.value
    }
}