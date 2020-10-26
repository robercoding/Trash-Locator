package com.rober.papelerasvalencia.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar

class GPSBroadcastReceiver(private val view: View, private val locationManager: LocationManager) :
    BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i("SeeGps", "is on")
        } else {
            Log.i("SeeGps", "is off")
            Snackbar.make(
                view,
                "We can't get your actual location and display nearest trash around you",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}