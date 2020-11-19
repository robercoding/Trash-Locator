package com.rober.trashlocator.ui.fragments.maps.utils

import android.content.Context
import android.location.Location
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.TrashLocation

interface IGeoCoderManager {
    fun getSingleAddressLocation(location: Location) : AddressLocation
    fun getSingleTrashLocation(location: Location) : TrashLocation
}
