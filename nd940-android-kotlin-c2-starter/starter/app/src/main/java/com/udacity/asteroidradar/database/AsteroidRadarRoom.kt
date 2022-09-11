package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AsteroidDatabase::class, ImageOfTheDayDatabase::class],
    version = 1,
    exportSchema = false
)
abstract class AsteroidRadarRoom : RoomDatabase() {

    abstract val asteroidDao: AsteroidDao
    abstract val imageOfTheDayDao: ImageOfTheDayDao

    companion object {

        @Volatile
        private var INSTANCE: AsteroidRadarRoom? = null

        fun getDatabaseInstance(context: Context): AsteroidRadarRoom {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidRadarRoom::class.java,
                        "asteroid_radar_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
