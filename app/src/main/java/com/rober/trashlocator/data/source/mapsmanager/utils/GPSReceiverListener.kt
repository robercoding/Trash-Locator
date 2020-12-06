package com.rober.trashlocator.data.source.mapsmanager.utils

interface GPSReceiverListener {
    fun showLocationMessage(message: String, error: Boolean)
    fun hideLocationMessage()
}