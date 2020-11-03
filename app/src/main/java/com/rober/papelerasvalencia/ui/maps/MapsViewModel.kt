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
import com.rober.papelerasvalencia.models.Trash

class MapsViewModel : ViewModel() {

    var listTrash: MutableLiveData<List<Trash>> = MutableLiveData()
    lateinit var geoCoder: Geocoder

    fun getAdressesByName(nameLocation: String, context: Context) {
        geoCoder = Geocoder(context)

        val addresses = geoCoder.getFromLocationName(nameLocation, 4)
        for (address in addresses) {
            Log.i("SeeAddress", "PostalCode = ${address.postalCode}")
            Log.i("SeeAddress", "Country = ${address.countryName}")
            Log.i("SeeAddress", "Locality = ${address.locality}")
            Log.i("SeeAddress", "Latitude = ${address.latitude}")
            Log.i("SeeAddress", "Longitude = ${address.longitude}")
//            Log.i("SeeAddress", "Locality = ${address.}")

        }
    }

    fun getAddressByLocation(location: Location, context: Context) {
        geoCoder = Geocoder(context)

        val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)[0]
        Log.i("SeeAddress", "${address}")
        Log.i(
            "SeeAddress",
            "Locality = ${address.locality}, PostalCode = ${address.postalCode} Sublocality= ${address.subLocality}"
        )

    }

    fun getTrashCluster(googleMap: GoogleMap, currentLocation: Location, context: Context) {
        val layer = GeoJsonLayer(googleMap, R.raw.papeleras_canarias, context)

        val places = mutableListOf<Trash>()
        for (feature in layer.features) {
            //            val properties = feature.properties as PropertiesX

            Log.i("MapDeviceLocation", "Calculate")

            val latLng = feature.geometry.geometryObject as LatLng
            val locationPlace = Location("")
            locationPlace.latitude = latLng.latitude
            locationPlace.longitude = latLng.longitude

            val distance = currentLocation.distanceTo(locationPlace)
            Log.i("MapDeviceLocation", "${distance}")


//            listDistances.add(distance)
            if (distance < 100f) {
                val place = Trash(latLng.latitude, latLng.longitude, "Cluster?", "${distance}m")

                places.add(place)
            }
        }

        listTrash.value = places
//            layer.addLayerToMap()
    }
}