package com.rober.trashlocator.ui.fragments.maps.utils

import android.util.Log
import com.google.android.gms.maps.GoogleMap

interface IMapsManager {
    fun setGoogleMap(googleMap: GoogleMap)

    fun updateLocationUI()
    fun setMyLocationButton()
    fun getDeviceLocation()

}