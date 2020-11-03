package com.rober.papelerasvalencia.models.Geometry

import com.google.maps.android.data.Geometry

data class GeometrySantaCruz(
    val coordinates: List<Double>,
    val type: String
) : Geometry<GeometrySantaCruz> {
    override fun getGeometryType(): String {
        return type
    }

    override fun getGeometryObject(): GeometrySantaCruz {
        return this
    }
}