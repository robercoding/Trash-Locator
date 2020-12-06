package com.rober.trashlocator.data.source.mapsmanager.utils

import android.annotation.SuppressLint
import android.location.Criteria
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log

class CustomLocationManagerImpl constructor(
    private val locationManager: LocationManager
) : CustomLocationManager {
    private val TAG = javaClass.simpleName

    @SuppressLint("MissingPermission")
    override fun requestSingleUpdate(locationListener: LocationListener) {
        try {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
            criteria.powerRequirement = Criteria.POWER_LOW
            criteria.isAltitudeRequired = false
            criteria.isBearingRequired = false
            criteria.isCostAllowed = true
            criteria.horizontalAccuracy = Criteria.ACCURACY_HIGH
            criteria.verticalAccuracy = Criteria.ACCURACY_HIGH

            locationManager.requestSingleUpdate(criteria, locationListener, null)
        } catch (e: Exception) {
            val errorMessage = e.message
            if (errorMessage != null) {
                Log.e(TAG, errorMessage)
            }
        }
    }
}