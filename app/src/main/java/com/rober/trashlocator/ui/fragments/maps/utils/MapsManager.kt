package com.rober.trashlocator.ui.fragments.maps.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.rober.trashlocator.utils.listeners.CustomLocationListener
import com.rober.trashlocator.utils.listeners.interfaces.ICustomLocationListener

class MapsManager constructor(
    private val context: Context,
    private val googleMap: GoogleMap,
    private val permissionsManager: PermissionsManager,
    private val gpsManager: GPSManager,
    private val locationManager: LocationManager
) : GoogleMap.OnMyLocationButtonClickListener, ICustomLocationListener{
    private val TAG = "MapsManager"

    private var locationListener: LocationListener = CustomLocationListener(this)

    private fun updateLocationUI() {
//        if (!isGoogleMapInitialized()) return

        val isLocationOk = permissionsManager.checkLocationPermissionAndSettings()
        if (!isLocationOk) return

        try {
            if (permissionsManager.locationPermissionGranted && gpsManager.isGPSEnabled()) {
                setMyLocationButton(true)
                getDeviceLocation()
            } else {
                setMyLocationButton(false)
                permissionsManager.checkLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun setMyLocationButton(value: Boolean) {
        if (value) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsManager.requestLocationPermissions()
                return
            }

            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.setOnMyLocationButtonClickListener(this)
        } else {
            googleMap.isMyLocationEnabled = false
            googleMap.uiSettings.isMyLocationButtonEnabled = false
        }
    }

    private fun getDeviceLocation() {
        permissionsManager.checkLocationPermission()
        if (!permissionsManager.locationPermissionGranted) {
            Log.i(TAG, "Location Setting UI to false..")
            permissionsManager.requestLocationPermissions()
            googleMap.uiSettings?.isMyLocationButtonEnabled = false
            return
        }

        gpsManager.checkIfLocationGPSIsOn()
        if (!gpsManager.isGPSEnabled()) {
            Log.i(TAG, "GPS Setting UI to false..")
            gpsManager.requestGPSTurnOn()
            googleMap.uiSettings?.isMyLocationButtonEnabled = false
            return
        }

        try {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
            criteria.powerRequirement = Criteria.POWER_LOW
            criteria.isAltitudeRequired = false
            criteria.isBearingRequired = false
            criteria.isCostAllowed = true
            criteria.horizontalAccuracy = Criteria.ACCURACY_HIGH
            criteria.verticalAccuracy = Criteria.ACCURACY_HIGH

            locationManager.requestSingleUpdate(criteria, locationListener, null)
        } catch (e: Exception) {
            val errorMessage = e.message
            if (errorMessage != null) {
                Log.e(TAG, errorMessage)
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun updateCurrentLocation(location: Location) {

    }

    override fun requestLocationUpdate() {

    }

    override fun showLocationMessage(message: String, error: Boolean) {

    }

    override fun hideLocationMessage() {
    }
}