package com.rober.papelerasvalencia.listeners

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar

class CustomLocationListener(
    private val view: View,
    private val mMap: GoogleMap,
    private val trashListener: TrashListener
) : LocationListener {

    override fun onLocationChanged(location: Location?) {
        if (location == null) {
            return
        }
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), 20f
            )
        )
        //Fake location
        val currentLocationModified = Location("")
        currentLocationModified.longitude = -16.251763
        currentLocationModified.latitude = 28.463636
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    currentLocationModified.latitude,
                    currentLocationModified.longitude
                ), 12f
            )
        )
        trashListener.updateCurrentLocation(currentLocationModified)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        //
    }

    override fun onProviderEnabled(provider: String?) {
        when (provider) {
            LocationManager.GPS_PROVIDER -> {
                Snackbar.make(
                    view,
                    "Finding nearest trash around you..",
                    Snackbar.LENGTH_LONG
                )
//                        Toast.makeText(this, "Getting ")
            }
        }
    }

    override fun onProviderDisabled(provider: String?) {
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

    }
}