package com.rober.papelerasvalencia.models

data class TrashLocation(var streetName: String, var feature: String, var locality: String) {

    constructor() : this("", "", "")
}