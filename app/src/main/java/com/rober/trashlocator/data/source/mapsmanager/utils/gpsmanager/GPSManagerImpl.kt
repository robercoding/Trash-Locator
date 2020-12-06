package com.rober.trashlocator.data.source.mapsmanager.utils.gpsmanager

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rober.trashlocator.ui.MapsActivity
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class GPSManagerImpl @Inject constructor(
    @ActivityContext private val context: Context,
    private val locationManager: LocationManager
) : GPSManager {
    private val TAG = javaClass.simpleName

    private var dialogRequestGps: AlertDialog? = null
    private var isGPSEnabled = false

    override fun checkIfLocationGPSIsEnabled(): Boolean {
        try {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
            }
        }
        return isGPSEnabled
    }

    override fun requestGPSEnable() {
        if (dialogRequestGps?.isShowing == true) {
            return
        }

        val mapsActivity = if (context is MapsActivity) context else return

        dialogRequestGps = MaterialAlertDialogBuilder(mapsActivity)
            .setTitle("GPS is off")
            .setMessage("Do you want to enable GPS, so we display the nearest trash around you?")
            .setPositiveButton("Yes") { dialog, which ->
//                iGPSManagerListener.enableGPS()
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, which ->
//                iGPSManagerListener.rejectEnableGPS()
//                This should show the user a essage
//                val messageConnectionTV = binding.textLocationSettings
//                messageConnectionTV.setBackgroundColor(
//                    ContextCompat.getColor(
//                        messageConnectionTV.context,
//                        R.color.red
//                    )
//                )
//                messageConnectionTV.text = context.getString(R.string.location_error)
//                messageConnectionTV.setTextColor(
//                    ContextCompat.getColor(
//                        messageConnectionTV.context,
//                        R.color.white
//                    )
//                )
//                messageConnectionTV.show()
            }
            .show()
    }

    override fun isGPSEnabled(): Boolean {
        return isGPSEnabled
    }
}