package com.rober.trashlocator.data.source.mapsmanager.utils

import android.annotation.SuppressLint
import android.location.LocationListener

interface CustomLocationManager {
    @SuppressLint("MissingPermission")
    fun requestSingleUpdate(locationListener: LocationListener)
}