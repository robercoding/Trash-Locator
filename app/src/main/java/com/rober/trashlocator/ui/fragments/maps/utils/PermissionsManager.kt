package com.rober.trashlocator.ui.fragments.maps.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rober.trashlocator.R
import com.rober.trashlocator.utils.Constants
import com.rober.trashlocator.utils.hide
import com.rober.trashlocator.utils.show

class PermissionsManager(
    private val context : Context,
    private val activity: Activity,
    private val gpsManager: GPSManager
) {
    private val TAG ="PermissionsManager"
    var gpsEnabled = false

    var locationPermissionGranted = false
    var alreadyRequestLocationPermission = false

    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        }
    }
    fun checkLocationPermissionAndSettings(): Boolean {
        checkLocationPermission()
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
        Log.i(TAG, "location is granted")

        val gpsEnabled = gpsManager.checkIfLocationGPSIsOn()
        if (!gpsEnabled) {
            gpsManager.requestGPSTurnOn()
            return false
        }

        return true
    }

    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )
    }
}