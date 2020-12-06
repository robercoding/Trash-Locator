package com.rober.trashlocator.data.source.mapsmanager

import android.content.BroadcastReceiver
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.models.AddressLocation

interface MapsManager {
    val TAG: String

    fun setGoogleMap(googleMap: GoogleMap)
    fun setGoogleMapAndConfiguration(googleMap: GoogleMap)
    fun updateLocationUI()
    fun setUpdateLocationByAddressLocation(
        addressLocation: AddressLocation,
        addToLiveData: Boolean
    )

    //Get list addresses of addresses by name location and set on MutableLiveData
    suspend fun getListAddressesByName(nameLocation: String)
    fun registerReceiver(receiver: BroadcastReceiver)
    fun unregisterReceiver()
}