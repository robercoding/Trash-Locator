package com.rober.papelerasvalencia.models

import com.google.maps.android.data.Geometry

data class Geometry(
    val coordinates: List<Double>,
    val type: String
) : Geometry<com.rober.papelerasvalencia.models.Geometry> {
    override fun getGeometryType(): String {
        return type
    }

    override fun getGeometryObject(): com.rober.papelerasvalencia.models.Geometry {
        return this
    }
}