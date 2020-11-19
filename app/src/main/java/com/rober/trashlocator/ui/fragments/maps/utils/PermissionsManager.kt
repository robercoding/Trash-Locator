package com.rober.trashlocator.ui.fragments.maps.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rober.trashlocator.R
import com.rober.trashlocator.ui.MapsActivity
import com.rober.trashlocator.utils.Constants
import com.rober.trashlocator.utils.hide
import com.rober.trashlocator.utils.show
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class PermissionsManager @Inject constructor(
    @ActivityScoped private val context: Context,
    private val gpsManager: GPSManager
) {
    private val TAG ="PermissionsManager"
    var gpsEnabled = false

    private var locationPermissionGranted = false
    var alreadyRequestLocationPermission = false

    fun checkLocationPermission() : Boolean{
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED)
    }
    fun checkLocationPermissionAndSettings(): Boolean {
        locationPermissionGranted = checkLocationPermission()
        if (!locationPermissionGranted && !alreadyRequestLocationPermission) {
            requestLocationPermissions()
            alreadyRequestLocationPermission = true
            return false
        }

        if (!locationPermissionGranted && alreadyRequestLocationPermission) {
//            binding.textPermissionApp.text = getString(R.string.location_permission_error)
//            binding.textPermissionApp.show()
//            binding.textPermissionApp.setBackgroundColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.red
//                )
//            )
            return false
        }

//        binding.textPermissionApp.hide()
//        Log.i(TAG, "location is granted")
//
//        val gpsEnabled = gpsManager.checkIfLocationGPSIsEnabled()
//        if (!gpsEnabled) {
//            gpsManager.checkIfLocationGPSIsEnabled()
//            return false
//        }

        return true
    }

    fun requestLocationPermissions() {
        val activity = if(context is MapsActivity) context else throw Exception("Can't get instance of MapsActivity")

        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )
    }

    fun isLocationPermissionGranted() : Boolean {
        return isLocationPermissionGranted()
    }

    fun setLocationPermissionGranted(isLocationPermissionGranted: Boolean) {
        locationPermissionGranted = isLocationPermissionGranted
    }
}