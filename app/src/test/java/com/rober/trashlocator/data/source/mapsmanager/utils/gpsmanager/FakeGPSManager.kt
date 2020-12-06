package com.rober.trashlocator.data.source.mapsmanager.utils.gpsmanager

class FakeGPSManager : GPSManager {
    var isGpsOn = true

    override fun checkIfLocationGPSIsEnabled(): Boolean {
        return isGpsOn
    }
    override fun requestGPSEnable() {

    }

    override fun isGPSEnabled(): Boolean {
        return isGpsOn
    }

    fun setGpsValue(value: Boolean) {
        isGpsOn = value
    }
}