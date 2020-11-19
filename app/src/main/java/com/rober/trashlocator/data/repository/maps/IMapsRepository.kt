package com.rober.trashlocator.data.repository.maps

import android.location.Location
import com.google.android.gms.maps.GoogleMap

interface IMapsRepository {
    var location: Location?

    fun setGoogleMap(googleMap: GoogleMap)

    fun updateLocationUI()
}