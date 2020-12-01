package com.rober.trashlocator.data.source.mapsmanager.utils

import android.annotation.SuppressLint
import android.location.LocationListener

import com.rober.trashlocator.utils.listeners.CustomLocationListener

interface ICustomLocationManager {
    @SuppressLint("MissingPermission")
    fun requestSingleUpdate(locationListener: LocationListener)
}