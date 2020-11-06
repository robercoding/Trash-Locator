package com.rober.trashlocator.models

import android.location.Location
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddressLocation(
    var localityName: String,
    var localityAdminAreaName: String,
    var streetName: String,
    var location: Location
) : Parcelable {

    constructor() : this("", "", "", Location(""))
}