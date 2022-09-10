package com.udacity.asteroidradar.utils

import java.text.SimpleDateFormat
import java.util.*

class DateFormatter {

    companion object {

        private val LOCALE = Locale.US

        fun getCurrentDateFormatted(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", LOCALE)
            return formatter.format(Date()).toString()
        }

        fun getCurrentExtendedDateFormatted(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", LOCALE)
            return formatter.format(Date()).toString()
        }
    }
}