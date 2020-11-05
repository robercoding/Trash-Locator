package com.rober.papelerasvalencia.models

import android.location.Location

data class AddressLocation(
    var localityName: String,
    var localityAdminAreaName: String,
    var streetName: String,
    var location: Location
) {

    constructor() : this("", "", "", Location(""))
}