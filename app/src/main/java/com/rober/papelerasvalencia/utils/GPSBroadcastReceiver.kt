package com.rober.papelerasvalencia.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Handler
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.rober.papelerasvalencia.R
import com.rober.papelerasvalencia.utils.listeners.interfaces.ICustomLocationListener

class GPSBroadcastReceiver(
    private val messageConnectionTV: TextView,
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

            messageConnectionTV.show()
            messageConnectionTV.text = context?.getString(R.string.location_works)
            messageConnectionTV.setBackgroundColor(
                ContextCompat.getColor(
                    messageConnectionTV.context,
                    R.color.green
                )
            )

            iCustomLocationListener.requestLocationUpdate()
            runnable = Runnable { messageConnectionTV.hide() }
            handler?.postDelayed(runnable!!, 3000)
        } else {
            if (runnable != null)
                handler?.removeCallbacks(runnable!!)

            messageConnectionTV.setBackgroundColor(
                ContextCompat.getColor(
                    messageConnectionTV.context,
                    R.color.red
                )
            )
            messageConnectionTV.text = context?.getString(R.string.location_error)
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