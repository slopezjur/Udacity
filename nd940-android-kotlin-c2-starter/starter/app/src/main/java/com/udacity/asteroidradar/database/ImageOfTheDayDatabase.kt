package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.ImageOfTheDay

@Entity(tableName = "image_of_the_day_table")
data class ImageOfTheDayDatabase constructor(
    @PrimaryKey
    val id: Long = 0,
    val url: String,
    val mediaType: String,
    val title: String
)

fun ImageOfTheDayDatabase.asDomainModel(): ImageOfTheDay {
    return ImageOfTheDay(
        url = this.url,
        mediaType = this.mediaType,
        title = this.title
    )
}

fun ImageOfTheDay.asDatabaseModel(): ImageOfTheDayDatabase {
    return ImageOfTheDayDatabase(
        url = this.url,
        mediaType = this.mediaType,
        title = this.title
    )
}
