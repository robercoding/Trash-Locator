package com.rober.papelerasvalencia.utils.listeners

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.rober.papelerasvalencia.utils.listeners.interfaces.TrashListener

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