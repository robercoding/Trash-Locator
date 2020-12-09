package com.rober.trashlocator.utils.listeners.interfaces

import android.location.Location

interface CustomLocationListener {
    fun updateCurrentLocation(location: Location)
    fun requestLocationUpdate()
}