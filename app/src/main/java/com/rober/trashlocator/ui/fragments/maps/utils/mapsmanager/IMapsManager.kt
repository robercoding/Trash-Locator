package com.rober.trashlocator.ui.fragments.maps.utils.mapsmanager

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.GoogleMap

interface IMapsManager {
    fun setGoogleMap(googleMap: GoogleMap)

    fun updateLocationUI()
    fun setMyLocationButton()
    fun getDeviceLocation()
}