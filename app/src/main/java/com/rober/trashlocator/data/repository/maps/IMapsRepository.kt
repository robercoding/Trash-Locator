package com.rober.trashlocator.data.repository.maps

import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.models.AddressLocation

interface IMapsRepository {
    var addressLocation: LiveData<AddressLocation>

    fun setGoogleMap(googleMap: GoogleMap)

    fun updateLocationUI()
}