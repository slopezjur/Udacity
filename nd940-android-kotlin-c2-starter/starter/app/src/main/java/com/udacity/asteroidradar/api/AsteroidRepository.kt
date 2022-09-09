/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.udacity.asteroidradar.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.ImageOfTheDay
import com.udacity.asteroidradar.database.AsteroidRoom
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AsteroidRepository(
    private val remoteDataSource: RemoteDataSource,
    private val database: AsteroidRoom
) {
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    suspend fun getImageOfTheDay(): ImageOfTheDay {
        return withContext(Dispatchers.IO) {
            remoteDataSource.getImageOfTheDay()
        }
    }

    suspend fun getAsteroids() {
        return withContext(Dispatchers.IO) {
            val asteroids = remoteDataSource.getAsteroidsByRange("", "")
            database.asteroidDao.insertAll(asteroids.asDatabaseModel())

            val testA = database.asteroidDao.getAsteroids().value?.asDomainModel()

            asteroids.asDatabaseModel().forEach {
                database.asteroidDao.insert(it)
            }

            val testB = database.asteroidDao.getAsteroids().value?.asDomainModel()

            val sizeA = testA?.size
            val sizeB = testB?.size
        }
    }
}
