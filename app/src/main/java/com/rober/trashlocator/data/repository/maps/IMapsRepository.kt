package com.rober.trashlocator.data.repository.maps

import android.content.BroadcastReceiver
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.utils.Event

interface IMapsRepository {
    var addressLocation: LiveData<AddressLocation>
    var message: LiveData<Event<String>>

    fun setGoogleMap(googleMap: GoogleMap)
    fun setGoogleMapAndConfiguration(googleMap: GoogleMap)

    fun updateLocationUI()
    fun setUpdateLocationByAddressLocation(addressLocation: AddressLocation, addToLiveData : Boolean)
    fun requestLocationUpdate()

    fun registerReceiver(broadcastReceiver: BroadcastReceiver)
    fun unregisterReceiver()
}