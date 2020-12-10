package com.rober.trashlocator.data.source.mapsmanager.utils.gps

interface GpsUtils {
    fun requestGPSEnable()
    fun isGPSEnabled(): Boolean
}