package com.rober.trashlocator.data.source.mapsmanager.utils.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rober.trashlocator.ui.MapsActivity
import com.rober.trashlocator.data.source.mapsmanager.utils.gpsmanager.GPSManagerImpl
import com.rober.trashlocator.utils.Constants
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class PermissionsManagerImpl @Inject constructor(
    @ActivityScoped private val context: Context,
    private val gpsManager: GPSManagerImpl
) : PermissionsManager {
    private val TAG = "PermissionsManager"
    override var gpsEnabled = false

    private var locationPermissionGranted = false
    override var alreadyRequestLocationPermission = false

    override fun checkLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED)
    }

    override fun checkLocationPermissionAndSettings(): Boolean {
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

    override fun requestLocationPermissions() {
        val activity =
            if (context is MapsActivity) context else throw Exception("Can't get instance of MapsActivity")

        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )
    }

    override fun isLocationPermissionGranted(): Boolean {
        return isLocationPermissionGranted()
    }

    override fun setLocationPermissionGranted(isLocationPermissionGranted: Boolean) {
        locationPermissionGranted = isLocationPermissionGranted
    }
}