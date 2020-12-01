package com.rober.trashlocator.data.source.mapsmanager.utils.gpsmanager

interface IGPSManager {
    fun checkIfLocationGPSIsEnabled(): Boolean
    fun requestGPSEnable()
    fun isGPSEnabled(): Boolean
}