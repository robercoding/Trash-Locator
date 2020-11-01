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
    private val trashListener: TrashListener
) : LocationListener {

    override fun onLocationChanged(location: Location?) {
        if (location == null) {
            return
        }

        trashListener.updateCurrentLocation(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        //
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

    }
}