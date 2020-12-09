package com.rober.trashlocator.data.source.mapsmanager.utils.gpsmanager

interface GpsUtils {
    fun checkIfLocationGPSIsEnabled(): Boolean
    fun requestGPSEnable()
    fun isGPSEnabled(): Boolean
}