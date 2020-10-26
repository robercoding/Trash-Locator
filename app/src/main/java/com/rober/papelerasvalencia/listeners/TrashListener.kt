package com.rober.papelerasvalencia.listeners

import android.location.Location

interface TrashListener {
    fun updateCurrentLocation(location: Location)
    fun getTrashAround ()
    fun getTrashSantaCruz()
}