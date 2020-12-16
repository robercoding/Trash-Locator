package com.rober.trashlocator.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Handler
import com.rober.trashlocator.R
import com.rober.trashlocator.data.source.mapsmanager.utils.GPSReceiverListener

class LocationBroadcastReceiver(
    private val locationManager: LocationManager,
    private val mGPSReceiverListener: GPSReceiverListener,
) : BroadcastReceiver() {

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    init {
        handler = Handler()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val isProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        showLocationMessage(isProviderEnabled, context)
    }

    fun showLocationMessage(isProviderEnabled: Boolean, context: Context?) {
        if (runnable != null)
            handler?.removeCallbacks(runnable!!)

        if (isProviderEnabled) {
            mGPSReceiverListener.showLocationMessage(
                context?.getString(R.string.location_works)!!,
                false
            )
            runnable = Runnable { mGPSReceiverListener.hideLocationMessage() }
            handler?.postDelayed(runnable!!, 3000)
        } else {
            mGPSReceiverListener.showLocationMessage(
                context?.getString(R.string.location_error)!!,
                true
            )

            runnable = Runnable { mGPSReceiverListener.hideLocationMessage() }
            handler?.postDelayed(runnable!!, 3000)

        }
    }
}