package com.rober.trashlocator.utils.listeners.interfaces

import android.location.Location

interface ICustomLocationListener {
    fun updateCurrentLocation(location: Location)
    fun requestLocationUpdate()
    fun showLocationMessage(message: String, error: Boolean)
    fun hideLocationMessage()
}