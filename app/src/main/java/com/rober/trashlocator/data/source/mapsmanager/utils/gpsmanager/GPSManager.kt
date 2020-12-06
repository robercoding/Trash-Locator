package com.rober.trashlocator.data.source.mapsmanager.utils.gpsmanager

interface GPSManager {
    fun checkIfLocationGPSIsEnabled(): Boolean
    fun requestGPSEnable()
    fun isGPSEnabled(): Boolean
}