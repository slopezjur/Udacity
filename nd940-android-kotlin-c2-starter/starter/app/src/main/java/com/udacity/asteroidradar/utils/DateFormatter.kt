package com.udacity.asteroidradar.utils

import java.text.SimpleDateFormat
import java.util.*

class DateFormatter {

    companion object {

        private val LOCALE = Locale.US
        private const val ONE_DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24

        fun getCurrentDateFormatted(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", LOCALE)
            return formatter.format(Date()).toString()
        }

        fun getCurrentDatePlusExtraDaysFormatted(extraDays: Int): String {
            val currentDate = Date()
            val futureDate = Date(currentDate.time + ((ONE_DAY_IN_MILLISECONDS) * extraDays))

            val formatter = SimpleDateFormat("yyyy-MM-dd", LOCALE)
            return formatter.format(futureDate).toString()
        }

        fun getCurrentExtendedDateFormatted(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", LOCALE)
            return formatter.format(Date()).toString()
        }
    }
}