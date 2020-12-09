package com.rober.trashlocator.utils.listeners

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.rober.trashlocator.utils.listeners.interfaces.CustomLocationListener


class CustomLocationListenerImpl(
    private val customLocationListener: CustomLocationListener
) : LocationListener {

    override fun onLocationChanged(location: Location?) {
        if (location == null) {
            return
        }

        customLocationListener.updateCurrentLocation(location)
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