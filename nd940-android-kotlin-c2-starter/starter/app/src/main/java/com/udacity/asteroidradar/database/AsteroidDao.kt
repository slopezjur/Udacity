package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidDao {

    @Query("select * from asteroid_table ORDER BY closeApproachDate ASC")
    fun getAsteroids(): LiveData<List<AsteroidDatabase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(asteroids: List<AsteroidDatabase>)

    @Query("DELETE FROM asteroid_table WHERE closeApproachDate < :range")
    suspend fun deteleByRange(range: String)
}