package com.udacity.project4.locationreminders.data.dto


/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and statusCode
 */
sealed class ResultState<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultState<T>()
    data class Error(val exception: Exception, val statusCode: Int? = null) :
        ResultState<Nothing>()
}