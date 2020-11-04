package com.rober.papelerasvalencia.models

data class AddressLocation(var streetName: String, var latitude: Double, var longitude: Double) {

    constructor() : this("", 0.0, 0.0)
}