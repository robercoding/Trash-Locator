package com.rober.trashlocator.data.source.mapsmanager.utils

import android.location.Location
import android.location.LocationListener
import com.rober.trashlocator.utils.listeners.interfaces.CustomLocationListener

class FakeCustomLocationManager(
    private var customLocationListener : CustomLocationListener? = null
) : CustomLocationManager{

    override fun requestSingleUpdate(locationListener: LocationListener) {
        val location = Location("")
        location.altitude = 0.0
        location.latitude = 0.0
        customLocationListener?.updateCurrentLocation(location)
    }

    fun setCustomLocationListener(newCustomLocationListener : CustomLocationListener){
        customLocationListener = newCustomLocationListener
    }
}