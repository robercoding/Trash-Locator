package com.rober.trashlocator.models

data class TrashLocation(var streetName: String, var feature: String, var locality: String) {
    constructor() : this("", "", "")
}