package com.rober.trashlocator.data.source.mapsmanager.utils

interface IGPSReceiverListener {
    fun showLocationMessage(message: String, error: Boolean)
    fun hideLocationMessage()
}