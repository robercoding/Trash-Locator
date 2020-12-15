package com.rober.trashlocator.data.repository.maps

import android.content.BroadcastReceiver
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.data.source.mapsmanager.MapsManagerImpl
import com.rober.trashlocator.utils.Event
import javax.inject.Inject

class MapsRepositoryImpl @Inject constructor(
    private val mapsManager: MapsManagerImpl
) : MapsRepository {

    override var addressLocation: LiveData<AddressLocation> = mapsManager.addressLocation
    override var listAddressesLocation: LiveData<Event<List<AddressLocation>>> =
        mapsManager.addressesLocation
    override var cameraMove: LiveData<Event<Boolean>> = mapsManager.cameraMove
    override var message: LiveData<Event<String>> = mapsManager.message

    override fun setGoogleMap(googleMap: GoogleMap) = mapsManager.setGoogleMap(googleMap)
    override fun setGoogleMapAndConfiguration(googleMap: GoogleMap) =
        mapsManager.setGoogleMapAndConfiguration(googleMap)

    override fun updateLocationUI() = mapsManager.updateLocationUI()
    override fun setUpdateLocationByAddressLocation(
        addressLocation: AddressLocation,
        addToLiveData: Boolean
    ) = mapsManager.setUpdateLocationByAddressLocation(addressLocation, addToLiveData)

    override fun requestLocationUpdate() = mapsManager.requestLocationUpdate()
    override fun enableMyLocationButton()  = mapsManager.enableMyLocationButton()

    override suspend fun getListAddressesByName(nameLocation: String) =
        mapsManager.getListAddressesByName(nameLocation)

    override fun registerReceiver(broadcastReceiver: BroadcastReceiver) =
        mapsManager.registerReceiver(broadcastReceiver)

    override fun unregisterReceiver() = mapsManager.unregisterReceiver()
}