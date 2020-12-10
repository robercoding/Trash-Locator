package com.rober.trashlocator.data.source.mapsmanager.utils.gps

class FakeGpsUtils : GpsUtils {
    var isGpsOn = true

    override fun requestGPSEnable() {

    }

    override fun isGPSEnabled(): Boolean {
        return isGpsOn
    }

    fun setGpsValue(value: Boolean) {
        isGpsOn = value
    }
}