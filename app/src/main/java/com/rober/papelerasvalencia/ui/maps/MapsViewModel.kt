package com.rober.papelerasvalencia.ui.maps

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.rober.papelerasvalencia.R
import com.rober.papelerasvalencia.models.Trash
import com.rober.papelerasvalencia.utils.CustomClusterRenderer

class MapsViewModel() : ViewModel() {

    var listTrash : MutableLiveData<List<Trash>> = MutableLiveData()

    fun getAdressesByName(nameLocation : String){

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