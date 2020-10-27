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
import com.google.android.material.snackbar.Snackbar
import com.rober.papelerasvalencia.R

class GPSBroadcastReceiver(private val messageConnectionTV: TextView, private val locationManager: LocationManager) :
    BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            messageConnectionTV.visibility = View.VISIBLE
            messageConnectionTV.text = "Location is back!"
            messageConnectionTV.setBackgroundColor(ContextCompat.getColor(messageConnectionTV.context, R.color.green))

            Handler().postDelayed(Runnable {
                messageConnectionTV.visibility = View.GONE
            }, 3000)
        } else {
            messageConnectionTV.setBackgroundColor(ContextCompat.getColor(messageConnectionTV.context, R.color.red))
            messageConnectionTV.text = "GPS is disconnected :("
            messageConnectionTV.setTextColor(ContextCompat.getColor(messageConnectionTV.context, R.color.white))
            messageConnectionTV.visibility = View.VISIBLE
        }
    }
}