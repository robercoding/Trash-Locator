package com.rober.trashlocator.ui.fragments.maps.utils

interface IGPSReceiverListener {
    fun showLocationMessage(message: String, error: Boolean)
    fun hideLocationMessage()
}