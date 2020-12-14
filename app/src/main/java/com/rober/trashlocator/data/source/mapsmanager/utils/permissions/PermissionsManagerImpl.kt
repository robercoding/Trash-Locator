package com.rober.trashlocator.data.source.mapsmanager.utils.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.rober.trashlocator.ui.MapsActivity
import com.rober.trashlocator.data.source.mapsmanager.utils.gps.GpsUtilsImpl
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PermissionsManagerImpl @Inject constructor(
    @ActivityScoped private val context: Context,
    private val gpsUtils: GpsUtilsImpl
) : PermissionsManager {
    private val TAG = "PermissionsManager"

    private var locationPermissionGranted = false
    override var alreadyRequestLocationPermission = false

    override fun checkLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED)
    }

    override fun requestLocationPermissions() {
        val activity =
            if (context is MapsActivity) context else throw Exception("Can't get instance of MapsActivity")
        val requestPermissions = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
        activity.requestPermissions(requestPermissions)
    }

    override fun isLocationPermissionGranted(): Boolean {
        return isLocationPermissionGranted()
    }

    override fun setLocationPermissionGranted(isLocationPermissionGranted: Boolean) {
        locationPermissionGranted = isLocationPermissionGranted
    }
}