package com.rober.trashlocator.data.repository.maps

import android.content.BroadcastReceiver
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.models.AddressLocation

interface IMapsRepository {
    var addressLocation: LiveData<AddressLocation>

    fun setGoogleMap(googleMap: GoogleMap)
    fun setGoogleMapAndConfiguration(googleMap: GoogleMap)

    fun updateLocationUI()
    fun setUpdateLocationByAddressLocation(addressLocation: AddressLocation, addToLiveData : Boolean)
    fun requestLocationUpdate()

    fun registerReceiver(broadcastReceiver: BroadcastReceiver)
    fun unregisterReceiver()
}