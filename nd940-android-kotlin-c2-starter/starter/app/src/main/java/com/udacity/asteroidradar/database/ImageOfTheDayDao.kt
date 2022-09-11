package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.ImageOfTheDay

@Dao
interface ImageOfTheDayDao {

    @Query("select * from image_of_the_day_table")
    fun getImageOfTheDay(): LiveData<ImageOfTheDayDatabase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(imageOfTheDay: ImageOfTheDayDatabase)
}