package com.udacity.asteroidradar.api

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.AsteroidRadarRoom
import com.udacity.asteroidradar.database.ImageOfTheDayDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.utils.DateFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AsteroidRadarRepository(
    private val remoteDataSource: RemoteDataSource,
    private val database: AsteroidRadarRoom
) {

    fun getImageOfTheDay(): LiveData<ImageOfTheDayDatabase> {
        return database.imageOfTheDayDao.getImageOfTheDay()
    }

    suspend fun refreshImageOfTheDay() {
        return withContext(Dispatchers.IO) {
            database.imageOfTheDayDao.insert(
                remoteDataSource.getImageOfTheDay().asDatabaseModel()
            )
        }
    }

    fun getAsteroids(): LiveData<List<AsteroidDatabase>> {
        return database.asteroidDao.getAsteroids()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.insertAll(
                remoteDataSource.getAsteroidsByRange(
                    DateFormatter.getCurrentDateFormatted(), ""
                ).asDatabaseModel()
            )
        }
    }

    suspend fun deleteByRange(range: String) {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deteleByRange(range)
        }
    }
}
