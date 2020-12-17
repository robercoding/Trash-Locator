package com.rober.trashlocator.data.source.mapsmanager.extensionutility

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.Trash
import com.rober.trashlocator.models.TrashLocation

interface MapsExtensionUtility {
    fun getSingleTrashLocation(location: Location): TrashLocation
    suspend fun getListAddressesByName(nameLocation: String): List<AddressLocation>
    suspend fun existsDataSet(addressLocation: AddressLocation): Boolean
    suspend fun getTrashCluster(
        googleMap: GoogleMap?,
        addressLocation: AddressLocation
    ): List<Trash>
}
