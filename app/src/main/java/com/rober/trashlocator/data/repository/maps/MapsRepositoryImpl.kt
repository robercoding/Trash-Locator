package com.rober.trashlocator.data.repository.maps

import android.content.BroadcastReceiver
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.ui.fragments.maps.utils.mapsmanager.MapsManager
import com.rober.trashlocator.utils.Event
import javax.inject.Inject

class MapsRepositoryImpl @Inject constructor(
    private val mapsManager: MapsManager
) : IMapsRepository {

    override var addressLocation: LiveData<AddressLocation> = mapsManager.addressLocation
    override var cameraMove: LiveData<Event<Boolean>> = mapsManager.cameraMove
    override var message: LiveData<Event<String>> = mapsManager.message

    override fun setGoogleMap(googleMap: GoogleMap) = mapsManager.setGoogleMap(googleMap)
    override fun setGoogleMapAndConfiguration(googleMap: GoogleMap) = mapsManager.setGoogleMapAndConfiguration(googleMap)

    override fun updateLocationUI() = mapsManager.updateLocationUI()
    override fun setUpdateLocationByAddressLocation(addressLocation: AddressLocation, addToLiveData : Boolean) = mapsManager.setUpdateLocationByAddressLocation(addressLocation, addToLiveData)
    override fun requestLocationUpdate() = mapsManager.requestLocationUpdate()

    override fun registerReceiver(broadcastReceiver: BroadcastReceiver) = mapsManager.registerReceiver(broadcastReceiver)

    override fun unregisterReceiver() = mapsManager.unregisterReceiver()
}