package com.rober.trashlocator.ui.fragments.maps.utils.mapsmanager.extensionutility

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.Trash
import com.rober.trashlocator.models.TrashLocation
import com.rober.trashlocator.ui.fragments.maps.utils.TrashLocationUtils
import com.rober.trashlocator.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MapsExtensionUtilityManager constructor(
    private val context: Context,
    private val geoCoder: Geocoder,
    private val trashLocationUtils: TrashLocationUtils
) : IMapsExtensionUtilityManager {

    override fun getSingleAddressLocation(location: Location): AddressLocation {
        val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)[0]
        Log.i("SeeAddress", "$address")

        val addressLocation = AddressLocation()
        addressLocation.localityName = address.locality
        addressLocation.localityAdminAreaName = address.adminArea
        addressLocation.location = location

        return addressLocation
    }

    override fun getSingleTrashLocation(location: Location): TrashLocation {
        val address = geoCoder.getFromLocation(location.latitude, location.longitude, 4)[0]

        var trashLocation = TrashLocation()

        trashLocation.streetName = if (address.thoroughfare == null) "" else address.thoroughfare
        trashLocation.locality = if (address.locality == null) "" else address.locality

        val isFeatureNameNumber = Utils.isNumber(address.featureName)
        if (isFeatureNameNumber)
            trashLocation.feature = address.featureName

        trashLocation = trashLocationUtils.addCommaTrashLocation(trashLocation, context)
        return trashLocation
    }

    override suspend fun existsDataSet(addressLocation: AddressLocation): Boolean {
        return trashLocationUtils.getDataset(addressLocation) > -1
    }

    override suspend fun getTrashCluster(
        googleMap: GoogleMap,
        addressLocation: AddressLocation
    ): List<Trash> {
        val raw = trashLocationUtils.getDataset(addressLocation)

        val places = mutableListOf<Trash>()
        withContext(Dispatchers.IO) {
            if (raw == -1) {
//                _message.postValue(Event(context.getStringResources(R.string.dataset_not_found)))
//            val message = context.resources.getString(R.string.dataset_not_found)
//            _message.value = Event(message)
                return@withContext
            }

//            _message.postValue(Event(context.getStringResources(R.string.dataset_found)))

            val layer = GeoJsonLayer(googleMap, raw, context)

            for (feature in layer.features) {

                Log.i("MapDeviceLocation", "Calculate")

                val latLng = feature.geometry.geometryObject as LatLng
                val locationPlace = Location("")
                locationPlace.latitude = latLng.latitude
                locationPlace.longitude = latLng.longitude

                val distance = addressLocation.location.distanceTo(locationPlace)
                Log.i("MapDeviceLocation", "${distance}")

//            listDistances.add(distance)
                if (distance < 100f) {
                    //Get info street name where this trash is.
                    val trashLocation = getSingleTrashLocation(locationPlace)

                    val place = Trash(
                        latLng.latitude,
                        latLng.longitude,
                        "${trashLocation.streetName}${trashLocation.feature}${trashLocation.locality}",
                        "${distance}m"
                    )

                    places.add(place)
                }
            }

//            _listTrash.postValue(places)
        }
        return places
    }

    override suspend fun getListAddressesByName(nameLocation: String): List<AddressLocation> {
        val addresses = geoCoder.getFromLocationName(nameLocation, 5)
        val listAddressesLocation = mutableListOf<AddressLocation>()

        for (address in addresses) {
            val location = AddressLocation()

            val addressLine = address.getAddressLine(0)

            location.streetName = addressLine
            location.location.latitude = address.latitude
            location.location.longitude = address.longitude
            location.localityName = if (address.locality == null) "" else address.locality
            location.localityAdminAreaName =
                if (address.adminArea == null) "" else address.adminArea

            listAddressesLocation.add(location)
        }
        return listAddressesLocation
    }
}