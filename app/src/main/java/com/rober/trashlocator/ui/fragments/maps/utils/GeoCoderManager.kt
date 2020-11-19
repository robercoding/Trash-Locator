package com.rober.trashlocator.ui.fragments.maps.utils

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.TrashLocation
import com.rober.trashlocator.utils.Utils

class GeoCoderManager constructor(
    private val context: Context,
    private val geoCoder: Geocoder,
    private val trashLocationUtils: TrashLocationUtils
) : IGeoCoderManager {

    override fun getSingleAddressLocation(location: Location): AddressLocation {
        val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)[0]
        Log.i("SeeAddress", "$address")

        val addressLocation = AddressLocation()
        addressLocation.localityName = address.locality
        addressLocation.localityAdminAreaName = address.adminArea
        addressLocation.location = location

        return addressLocation
    }

    override fun getSingleTrashLocation(location: Location): TrashLocation {
        val address = geoCoder.getFromLocation(location.latitude, location.longitude, 4)[0]

        var trashLocation = TrashLocation()

        trashLocation.streetName = if (address.thoroughfare == null) "" else address.thoroughfare
        trashLocation.locality = if (address.locality == null) "" else address.locality

        val isFeatureNameNumber = Utils.isNumber(address.featureName)
        if (isFeatureNameNumber)
            trashLocation.feature = address.featureName

        trashLocation = trashLocationUtils.addCommaTrashLocation(trashLocation, context)
        return trashLocation
    }
}