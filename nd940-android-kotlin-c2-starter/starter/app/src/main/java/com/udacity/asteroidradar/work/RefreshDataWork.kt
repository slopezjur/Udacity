package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.api.AsteroidRadarRepository
import com.udacity.asteroidradar.api.RemoteDataSource
import com.udacity.asteroidradar.database.AsteroidRadarRoom.Companion.getDatabaseInstance
import com.udacity.asteroidradar.utils.DateFormatter
import retrofit2.HttpException

class RefreshDataWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {

        val remoteDataSource = RemoteDataSource()
        val database = getDatabaseInstance(applicationContext)
        val asteroidRadarRepository = AsteroidRadarRepository(remoteDataSource, database)

        return try {
            asteroidRadarRepository.refreshImageOfTheDay()
            asteroidRadarRepository.refreshAsteroids()
            asteroidRadarRepository.deleteByRange(DateFormatter.getCurrentDateFormatted())
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}
