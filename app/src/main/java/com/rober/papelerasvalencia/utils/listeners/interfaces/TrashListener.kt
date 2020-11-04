package com.rober.papelerasvalencia.utils.listeners.interfaces

import android.location.Location

interface TrashListener {
    fun updateCurrentLocation(location: Location)
    fun requestLocationUpdate()
}