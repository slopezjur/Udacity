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

    companion object {
        private const val EXTRA_DAYS = 7
        private const val IMAGE_MEDIA_TYPE = "image"
    }

    fun getImageOfTheDay(): LiveData<ImageOfTheDayDatabase> {
        return database.imageOfTheDayDao.getImageOfTheDay()
    }

    suspend fun refreshImageOfTheDay() {
        withContext(Dispatchers.IO) {
            val imageOfTheDay = remoteDataSource.getImageOfTheDay()
            if (imageOfTheDay.mediaType == IMAGE_MEDIA_TYPE) {
                database.imageOfTheDayDao.insert(
                    imageOfTheDay.asDatabaseModel()
                )
            }
        }
    }

    fun getAsteroids(): LiveData<List<AsteroidDatabase>> {
        return database.asteroidDao.getAsteroids()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.insertAll(
                remoteDataSource.getAsteroidsByRange(
                    DateFormatter.getCurrentDateFormatted(),
                    DateFormatter.getCurrentDatePlusExtraDaysFormatted(EXTRA_DAYS)
                ).asDatabaseModel()
            )
        }
    }

    suspend fun deleteByRange(range: String) {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deteleByRange(range)
        }
    }

    fun getWeekAsteroidsFromLocal(): LiveData<List<AsteroidDatabase>> {
        return database.asteroidDao.getWeekAsteroids(
            DateFormatter.getCurrentDatePlusExtraDaysFormatted(
                EXTRA_DAYS
            )
        )
    }

    fun getTodayAsteroidsFromLocal(): LiveData<List<AsteroidDatabase>> {
        return database.asteroidDao.getWeekAsteroids(
            DateFormatter.getCurrentDateFormatted()
        )
    }
}
