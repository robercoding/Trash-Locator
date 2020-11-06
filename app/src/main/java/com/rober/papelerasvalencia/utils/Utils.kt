package com.rober.papelerasvalencia.utils

import org.threeten.bp.Instant

object Utils {
    //Return boolean
    fun canUserRequestUpdateLocation(lastTimeRequestedInSeconds: Long): Boolean {
        if (lastTimeRequestedInSeconds.toInt() == -1) {
            return true
        }
        val timeNow = Instant.now().epochSecond

        val secondsDifference = timeNow - lastTimeRequestedInSeconds

        if (secondsDifference < 30) {
            return false
        }

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