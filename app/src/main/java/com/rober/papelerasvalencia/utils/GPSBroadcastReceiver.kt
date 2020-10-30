package com.rober.papelerasvalencia.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.rober.papelerasvalencia.R

class GPSBroadcastReceiver(
    private val messageConnectionTV: TextView,
    private val locationManager: LocationManager
) :
    BroadcastReceiver() {

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    init {
        handler = Handler()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i("MapsFragment", "ACTIVE!")
            messageConnectionTV.show()
            messageConnectionTV.text = "Location is back!"
            messageConnectionTV.setBackgroundColor(
                ContextCompat.getColor(
                    messageConnectionTV.context,
                    R.color.green
                )
            )

            runnable = Runnable { messageConnectionTV.hide() }
            handler?.postDelayed(runnable!!, 3000)
        } else {
            if (runnable != null)
                handler?.removeCallbacks(runnable!!)

            Log.i("MapsFragment", "not active")
            messageConnectionTV.setBackgroundColor(
                ContextCompat.getColor(
                    messageConnectionTV.context,
                    R.color.red
                )
            )
            messageConnectionTV.text = "GPS is disconnected :("
            messageConnectionTV.setTextColor(
                ContextCompat.getColor(
                    messageConnectionTV.context,
                    R.color.white
                )
            )
            messageConnectionTV.show()
        }
    }
}