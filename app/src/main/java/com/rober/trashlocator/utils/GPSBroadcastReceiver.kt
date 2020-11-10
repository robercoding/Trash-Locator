package com.rober.trashlocator.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Handler
import android.util.Log
import com.rober.trashlocator.R
import com.rober.trashlocator.utils.listeners.interfaces.ICustomLocationListener

class GPSBroadcastReceiver(
    private val locationManager: LocationManager,
    private val iCustomLocationListener: ICustomLocationListener
) :
    BroadcastReceiver() {

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    init {
        handler = Handler()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Log.i("SeeReceiver", "Connected!")

            iCustomLocationListener.requestLocationUpdate()
            iCustomLocationListener.showLocationMessage(
                context?.getString(R.string.location_works)!!,
                false
            )
            runnable = Runnable { iCustomLocationListener.hideLocationMessage() }
            handler?.postDelayed(runnable!!, 3000)
        } else {
            Log.i("SeeReceiver", "Disconnected!")
            if (runnable != null)
                handler?.removeCallbacks(runnable!!)

            iCustomLocationListener.showLocationMessage(
                context?.getString(R.string.location_error)!!,
                true
            )
        }
    }
}