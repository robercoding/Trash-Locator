package com.rober.trashlocator.data.repository.maps

import androidx.lifecycle.LiveData
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.utils.Event
import com.rober.trashlocator.utils.LocationBroadcastReceiver

interface MapsRepository {
    var addressLocation: LiveData<AddressLocation>
    var listAddressesLocation: LiveData<Event<List<AddressLocation>>>
    var cameraMove: LiveData<Event<Boolean>>
    var message: LiveData<Event<String>>

    fun setGoogleMap(googleMap: GoogleMap)
    fun setGoogleMapAndConfiguration(googleMap: GoogleMap)

    fun updateLocationUI()
    fun setUpdateLocationByAddressLocation(addressLocation: AddressLocation, addToLiveData: Boolean)
    fun requestLocationUpdate()
    fun enableMyLocationButton()

    suspend fun getListAddressesByName(nameLocation: String)

    fun registerReceiver(broadcastReceiver: LocationBroadcastReceiver)
    fun unregisterReceiver()
}