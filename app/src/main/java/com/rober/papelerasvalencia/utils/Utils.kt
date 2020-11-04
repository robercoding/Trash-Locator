package com.rober.papelerasvalencia.utils

import android.util.Log
import org.threeten.bp.Instant

object Utils {
    //Return boolean
    fun canUserRequestUpdateLocation(lastTimeRequestedInSeconds: Long): Boolean {
        if (lastTimeRequestedInSeconds.toInt() == -1) {
            Log.i("SeeSeconds", "True: -1")
            return true
        }
        val timeNow = Instant.now().epochSecond

        val secondsDifference = timeNow - lastTimeRequestedInSeconds

        if (secondsDifference < 30) {
            Log.i("SeeSeconds", "False: Seconds = ${secondsDifference}")
            return false
        }
        Log.i("SeeSeconds", "True: Seconds = ${secondsDifference}")

        return true
    }

    fun isNumber(text: String): Boolean {
        return try {
            text.toInt()
            true
        } catch (e: Exception) {
            false
        }
    }
}