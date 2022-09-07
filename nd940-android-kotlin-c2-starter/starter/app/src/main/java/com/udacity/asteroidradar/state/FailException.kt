package com.udacity.asteroidradar.state

sealed class FailException {
    object BadRequest : FailException()
    object EmptyBody : FailException()
}
