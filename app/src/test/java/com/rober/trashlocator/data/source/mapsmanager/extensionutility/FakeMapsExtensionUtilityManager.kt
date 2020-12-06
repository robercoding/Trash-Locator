package com.rober.trashlocator.data.source.mapsmanager.extensionutility

import android.location.Address
import android.location.Location
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.data.source.mapsmanager.utils.TrashLocationUtils
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.Trash
import com.rober.trashlocator.models.TrashLocation
import com.rober.trashlocator.utils.Utils
import java.util.*
import kotlin.collections.LinkedHashMap

class FakeMapsExtensionUtilityManager(
    private val trashLocationUtils: TrashLocationUtils
) : MapsExtensionUtilityManager {

    val listLocations = listOf<String>("Madrid", "Valencia")
    val listAddressLocation: LinkedHashMap<String, AddressLocation> = LinkedHashMap()
    val caceresLatitude = 39.4754331
    val caceresLongitude = -6.377640
    private var shouldAddTrashLocationFeature = false
    private var shouldAddTrashLocationLocality = false

    override fun getSingleTrashLocation(location: Location): TrashLocation {
        val address = getFromLocation(location)
        var trashLocation = TrashLocation()

        trashLocation.streetName = if (address.thoroughfare == null) "" else address.thoroughfare
        trashLocation.locality = if (address.locality == null) "" else address.locality

        val isFeatureNameNumber =
            if (address.featureName == null) false else Utils.isNumber(address.featureName)
        if (isFeatureNameNumber) {
            trashLocation.feature = address.featureName
        }

        trashLocation = trashLocationUtils.addCommaTrashLocation(
            trashLocation,
            ApplicationProvider.getApplicationContext()
        )

        println("locality is null? =${trashLocation.locality} ")
        return trashLocation
    }

    override suspend fun getListAddressesByName(nameLocation: String): List<AddressLocation> {
        if (listLocations.contains(nameLocation)) {
            val location = Location("")
            location.latitude = 40.410489
            location.longitude = -3.692454
            listAddressLocation[nameLocation] =
                AddressLocation(nameLocation, nameLocation, nameLocation, location)
            return listOf(AddressLocation(nameLocation, nameLocation, nameLocation, Location("")))
        }
        return listOf()
    }

    override suspend fun existsDataSet(addressLocation: AddressLocation): Boolean {
        return true
    }

    override suspend fun getTrashCluster(
        googleMap: GoogleMap?,
        addressLocation: AddressLocation
    ): List<Trash> {
        val location = Location("")
        location.latitude = caceresLatitude
        location.longitude = caceresLongitude
        val distance = addressLocation.location.distanceTo(location)

        return when (distance) {
            in 0f..100f -> listOf(Trash(location.latitude, 0.0, "Returns!", ""))
            else -> emptyList()
        }
    }

    fun setShouldAddTrashLocationFeature(value: Boolean) {
        shouldAddTrashLocationFeature = value
    }

    fun setShouldAddTrashLocationLocality(value: Boolean) {
        shouldAddTrashLocationLocality = value
    }

    private fun getFromLocation(location: Location): Address {
        println("Location latitude ${location.latitude}")
        if (location.latitude == caceresLatitude) {
            println("Caceres")
            val address = Address(Locale.getDefault())
            address.setAddressLine(0, "Plaza Mayor, 28, 10003 Cáceres, Spain")
            address.thoroughfare = "Plaza Obispo Galarza"
            address.adminArea = "Extremadura"
            address.subAdminArea = "Cáceres"
            if (shouldAddTrashLocationLocality)
                address.locality = "Cáceres"
            if (shouldAddTrashLocationFeature)
                address.featureName = "28"
            address.latitude = 39.4754331
            address.longitude = -6.371404
            return address
        } else {
            println("Canarias")
            val address = Address(Locale.getDefault())
            address.setAddressLine(
                0,
                "Av. de San Sebastián, 51, 38003 Santa Cruz de Tenerife, Spain"
            )
            address.thoroughfare = "Calle José Hernández Alfonso"
            address.adminArea = "Canarias"
            address.subAdminArea = "Santa Cruz de Tenerife"
            if (shouldAddTrashLocationFeature)
                address.featureName = "51"
            if (shouldAddTrashLocationLocality) {
                address.locality = "Santa Cruz de Tenerife"
            }
            address.latitude = 28.4633765
            address.longitude = -16.2519241
            return address
        }
    }
}