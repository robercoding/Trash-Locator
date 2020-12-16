package com.rober.trashlocator.data.source.mapsmanager.utils.gps

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient
import com.rober.trashlocator.utils.Constants
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
class GpsUtilsImpl(
    @ActivityContext private val context: Context,
    private val locationManager: LocationManager,
    private val settingsClient: SettingsClient
) : GpsUtils {
    private val TAG = javaClass.simpleName

    private val locationSettingsRequest: LocationSettingsRequest
    private val locationRequest: LocationRequest

    private var isGPSEnabled = false

    // method for turn on GPS
    fun turnGPSOn(onGpsListener: onGpsListener?) {
        if (isGPSEnabled()) {
            onGpsListener?.gpsStatus(true)
        } else {
            settingsClient //Access to settings client
                .checkLocationSettings(locationSettingsRequest) // Check Location settings client by creating a request to location
                .addOnSuccessListener((context as Activity)) { //  GPS is already enable, callback GPS status through listener
                    onGpsListener?.gpsStatus(true)
                }
                .addOnFailureListener(
                    context
                ) { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(
                                context,
                                Constants.GPS_REQUEST
                            )
                        } catch (sie: SendIntentException) {
                            Log.i(
                                ContentValues.TAG,
                                "PendingIntent unable to execute request."
                            )
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings."
                            Log.e(ContentValues.TAG, errorMessage)
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
        }
    }

//    override fun checkIfLocationGPSIsEnabled(): Boolean {
//        try {
//            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        } catch (e: Exception) {
//            e.message?.let {
//                Log.e(TAG, it)
//            }
//        }
//        return isGPSEnabled
//    }

    override fun requestGPSEnable() {
        turnGPSOn(object : onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                isGPSEnabled = isGPSEnable
            }
        })
    }

    override fun isGPSEnabled(): Boolean {
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return isGPSEnabled
    }

    interface onGpsListener {
        fun gpsStatus(isGPSEnable: Boolean)
    }

    init {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10 * 1000.toLong()
        locationRequest.fastestInterval = 2 * 1000.toLong()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build()
        builder.setAlwaysShow(true) //Needed
    }
}