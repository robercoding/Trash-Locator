package com.rober.trashlocator.ui.fragments.maps.utils.mapsmanager

import com.google.android.gms.maps.GoogleMap

interface IMapsManager {
    fun setGoogleMap(googleMap: GoogleMap)

    fun updateLocationUI()
    fun setMyLocationButton()
    fun getDeviceLocation()
}