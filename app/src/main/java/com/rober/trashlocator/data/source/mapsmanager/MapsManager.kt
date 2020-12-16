package com.rober.trashlocator.data.source.mapsmanager

import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.utils.LocationBroadcastReceiver

interface MapsManager {
    val TAG: String

    fun setGoogleMap(googleMap: GoogleMap)
    fun setGoogleMapAndConfiguration(googleMap: GoogleMap)
    fun updateLocationUI()
    fun setUpdateLocationByAddressLocation(
        addressLocation: AddressLocation,
        addToLiveData: Boolean
    )

    fun enableMyLocationButton()

    //Get list addresses of addresses by name location and set on MutableLiveData
    suspend fun getListAddressesByName(nameLocation: String)
    fun registerReceiver(receiver: LocationBroadcastReceiver)
    fun unregisterReceiver()
}