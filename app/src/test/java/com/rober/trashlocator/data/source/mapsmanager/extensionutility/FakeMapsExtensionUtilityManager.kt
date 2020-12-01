package com.rober.trashlocator.data.source.mapsmanager.extensionutility

import android.location.Location
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.data.source.mapsmanager.utils.TrashLocationUtils
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.Trash
import com.rober.trashlocator.models.TrashLocation

class FakeMapsExtensionUtilityManager(
    private val trashLocationUtils: TrashLocationUtils
) : IMapsExtensionUtilityManager {

    val listLocations = listOf<String>("Madrid", "Valencia")
    val listAddressLocation : LinkedHashMap<String, AddressLocation> = LinkedHashMap()

    override fun getSingleAddressLocation(location: Location): AddressLocation {
        return AddressLocation("Valencia", "Valencia", "A street!", location)
    }

    override fun getSingleTrashLocation(location: Location): TrashLocation {
        var trashLocation = TrashLocation()

        trashLocation.streetName = "Street"
        trashLocation.locality = "Locality"

        val isFeatureNameNumber = true
        if (isFeatureNameNumber)
            trashLocation.feature = "A feature"

        trashLocation = trashLocationUtils.addCommaTrashLocation(trashLocation, ApplicationProvider.getApplicationContext())

        return trashLocation
    }

    override suspend fun getListAddressesByName(nameLocation: String): List<AddressLocation> {
        if(listLocations.contains(nameLocation)){
            val location = Location("")
            location.latitude = 40.410489
            location.altitude = -3.692454
            listAddressLocation[nameLocation] = AddressLocation(nameLocation, nameLocation, nameLocation, location)
            return listOf(AddressLocation(nameLocation, nameLocation, nameLocation, Location("")))
        }
        return listOf()
    }

    override suspend fun existsDataSet(addressLocation: AddressLocation): Boolean {
        return true
    }

    override suspend fun getTrashCluster(
        googleMap: GoogleMap,
        addressLocation: AddressLocation
    ): List<Trash> {
        return emptyList<Trash>()
    }
}