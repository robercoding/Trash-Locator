package com.rober.trashlocator.ui.fragments.maps.utils

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.Trash
import com.rober.trashlocator.models.TrashLocation

interface IMapsExtensionUtilityManager {
    fun getSingleAddressLocation(location: Location) : AddressLocation
    fun getSingleTrashLocation(location: Location) : TrashLocation
    suspend fun getTrashCluster(googleMap: GoogleMap, addressLocation: AddressLocation):List<Trash>
}
