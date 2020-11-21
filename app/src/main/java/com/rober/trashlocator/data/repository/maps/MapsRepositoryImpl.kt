package com.rober.trashlocator.data.repository.maps

import android.content.BroadcastReceiver
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.ui.fragments.maps.utils.MapsManager
import javax.inject.Inject

class MapsRepositoryImpl @Inject constructor(
    private val mapsManager: MapsManager
) : IMapsRepository {

    override var addressLocation: LiveData<AddressLocation> = mapsManager.addressLocation

    override fun setGoogleMap(googleMap: GoogleMap) = mapsManager.setGoogleMap(googleMap)

    override fun updateLocationUI() = mapsManager.updateLocationUI()

    override fun registerReceiver(broadcastReceiver: BroadcastReceiver) = mapsManager.registerReceiver(broadcastReceiver)

    override fun unregisterReceiver() = mapsManager.unregisterReceiver()
}