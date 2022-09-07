package com.udacity.asteroidradar.main

import com.udacity.asteroidradar.Asteroid

class AsteroidClick(val block: (Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = block(asteroid)
}