package com.rober.papelerasvalencia.ui.maps

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.rober.papelerasvalencia.R
import com.rober.papelerasvalencia.models.AddressLocation
import com.rober.papelerasvalencia.models.Trash
import com.rober.papelerasvalencia.models.TrashLocation
import com.rober.papelerasvalencia.utils.Utils

class MapsViewModel : ViewModel() {

    val listTrash: MutableLiveData<List<Trash>> = MutableLiveData()
    val listAddressesLocation: MutableLiveData<List<AddressLocation>> = MutableLiveData()

    lateinit var geoCoder: Geocoder

    fun getAdressesByName(nameLocation: String, context: Context) {
        geoCoder = Geocoder(context)

        val addresses = geoCoder.getFromLocationName(nameLocation, 5)
        val mutableListAddressesLocation = mutableListOf<AddressLocation>()
        for (address in addresses) {
            val location = AddressLocation()

            val addressLine = address.getAddressLine(0)

            location.streetName = addressLine
            location.latitude = address.latitude
            location.longitude = address.longitude

            Log.i("SeeAddress", "Location = ${location}}")
            mutableListAddressesLocation.add(location)
        }

        listAddressesLocation.postValue(mutableListAddressesLocation.toList())
    }

    private fun getSingleAddressLocation(location: Location, context: Context): TrashLocation {
        geoCoder = Geocoder(context)

        val address = geoCoder.getFromLocation(location.latitude, location.longitude, 4)[0]

        var trashLocation = TrashLocation()

        trashLocation.streetName = if (address.thoroughfare == null) "" else address.thoroughfare
        trashLocation.locality = if (address.locality == null) "" else address.locality

        val isFeatureNameNumber = Utils.isNumber(address.featureName)
        if (isFeatureNameNumber)
            trashLocation.feature = address.featureName

        trashLocation = addCommaTrashLocation(trashLocation, context)
        return trashLocation
    }

    private fun getListAddressLocation(location: Location, context: Context): List<TrashLocation> {
        geoCoder = Geocoder(context)

        val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 4)

        val mutableListTrashLocation = mutableListOf<TrashLocation>()
        for (address in addresses) {
            Log.i(
                "SeeAddress",
                "Locality = ${address.locality}, PostalCode = ${address.postalCode} Sublocality= ${address.subLocality}"
            )
            val trashLocation = TrashLocation()
            trashLocation.streetName = address.thoroughfare
            trashLocation.locality = address.locality
            if (Utils.isNumber(address.featureName)) {
                trashLocation.feature = trashLocation.feature
            }

            mutableListTrashLocation.add(trashLocation)
        }

        return mutableListTrashLocation.toList()
    }

    //Add a comma if next word is available
    private fun addCommaTrashLocation(
        trashLocation: TrashLocation,
        context: Context
    ): TrashLocation {
        val isStreetNameAvailable = trashLocation.streetName.isNotBlank()
        val isFeatureNameAvailable = trashLocation.feature.isNotBlank()
        val isLocalityAvailable = trashLocation.locality.isNotBlank()

        if ((isStreetNameAvailable && isFeatureNameAvailable) || (isStreetNameAvailable && isLocalityAvailable)) {
            trashLocation.streetName = trashLocation.streetName + ", "
        }
        if (isFeatureNameAvailable && isLocalityAvailable) {
            trashLocation.feature = trashLocation.feature + ", "
        }

        if (!isStreetNameAvailable && !isFeatureNameAvailable) {
            trashLocation.streetName = context.resources.getString(R.string.trash_no_information)
            trashLocation.feature = ""
            trashLocation.locality = ""
        }
        return trashLocation
    }

    fun getTrashCluster(googleMap: GoogleMap, currentLocation: Location, context: Context) {
        val layer = GeoJsonLayer(googleMap, R.raw.papeleras_canarias, context)

        val places = mutableListOf<Trash>()
        for (feature in layer.features) {

            Log.i("MapDeviceLocation", "Calculate")

            val latLng = feature.geometry.geometryObject as LatLng
            val locationPlace = Location("")
            locationPlace.latitude = latLng.latitude
            locationPlace.longitude = latLng.longitude

            val distance = currentLocation.distanceTo(locationPlace)
            Log.i("MapDeviceLocation", "${distance}")

//            listDistances.add(distance)
            if (distance < 100f) {
                val trashLocation = getSingleAddressLocation(locationPlace, context)

                val place = Trash(
                    latLng.latitude,
                    latLng.longitude,
                    "${trashLocation.streetName}${trashLocation.feature}${trashLocation.locality}",
                    "${distance}m"
                )

                places.add(place)
            }
        }

        listTrash.value = places
    }
}