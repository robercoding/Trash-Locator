package com.rober.trashlocator.data.source.mapsmanager.utils

import android.location.Location
import android.location.LocationListener
import com.rober.trashlocator.utils.listeners.interfaces.ICustomLocationListener

class FakeCustomLocationManager(
    private var customLocationListener : ICustomLocationListener? = null
) : CustomLocationManager{

    override fun requestSingleUpdate(locationListener: LocationListener) {
        val location = Location("")
        location.altitude = 0.0
        location.latitude = 0.0
        customLocationListener?.updateCurrentLocation(location)
    }

    fun setCustomLocationListener(newCustomLocationListener : ICustomLocationListener){
        customLocationListener = newCustomLocationListener
    }
}