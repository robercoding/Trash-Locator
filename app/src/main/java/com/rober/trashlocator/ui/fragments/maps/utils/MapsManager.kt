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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.utils.listeners.CustomLocationListener
import com.rober.trashlocator.utils.listeners.interfaces.ICustomLocationListener

class MapsManager constructor(
    private val context: Context,
    private val permissionsManager: PermissionsManager,
    private val gpsManager: GPSManager,
    private val locationManager: LocationManager
) : GoogleMap.OnMyLocationButtonClickListener, ICustomLocationListener {
    private val TAG = "MapsManager"

    var location : Location? = null
    private var googleMap: GoogleMap? = null

    //    private var locationListener: LocationListener? = null
    private var locationListener: LocationListener? = CustomLocationListener(this)

    //    fun setGoogleMapAndLocationListener(googleMap: GoogleMap, customLocationListener: CustomLocationListener){
//        this.googleMap = googleMap
//        locationListener = customLocationListener
//    }
    fun setGoogleMap(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    fun updateLocationUI() {
//        if (!isGoogleMapInitialized()) return
        val isLocationPermissionsOk = permissionsManager.checkLocationPermissionAndSettings()
        val isGPSEnabled = gpsManager.checkIfLocationGPSIsEnabled()
        if (!isLocationPermissionsOk){
            permissionsManager.requestLocationPermissions()
            setMyLocationButton(false)
            return
        }

        if(!isGPSEnabled){
            gpsManager.requestGPSEnable()
            setMyLocationButton(false)
            return
        }

        try {
            if (isLocationPermissionsOk && isGPSEnabled) {
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

            googleMap?.isMyLocationEnabled = true
            googleMap?.uiSettings?.isMyLocationButtonEnabled = true
            googleMap?.setOnMyLocationButtonClickListener(this)
        } else {
            googleMap?.isMyLocationEnabled = false
            googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        }
    }

    private fun getDeviceLocation() {
        val isLocationPermissionsOk = permissionsManager.checkLocationPermission()
        if (!isLocationPermissionsOk) {
            permissionsManager.requestLocationPermissions()
            setMyLocationButton(false)
            return
        }

        val isGPSEnabled = gpsManager.checkIfLocationGPSIsEnabled()
        if (!isGPSEnabled) {
            Log.i(TAG, "GPS Setting UI to false..")
            gpsManager.requestGPSEnable()
            setMyLocationButton(false)
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

    private fun moveCameraByCameraPosition(cameraPosition: CameraPosition) {
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                cameraPosition.target, cameraPosition.zoom
            )
        )
    }

    override fun onMyLocationButtonClick(): Boolean {
        getDeviceLocation()
        return true
    }

    override fun updateCurrentLocation(location: Location) {
        val currentAddressLocation = AddressLocation()
        currentAddressLocation.location = location

//        if (!isAdded) {
//            hasBeenDetached = true
//            return
//        }

    }

    override fun requestLocationUpdate() {

    }

    override fun showLocationMessage(message: String, error: Boolean) {

    }

    override fun hideLocationMessage() {
    }
}