package com.rober.trashlocator.data.repository.maps

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.ui.fragments.maps.utils.MapsManager
import javax.inject.Inject

class MapsRepositoryImpl @Inject constructor(
    private val mapsManager: MapsManager
) : IMapsRepository {

    override var location: Location? = mapsManager.location

    override fun setGoogleMap(googleMap: GoogleMap) = mapsManager.setGoogleMap(googleMap)

    override fun updateLocationUI() = mapsManager.updateLocationUI()
}