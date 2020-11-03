package com.rober.papelerasvalencia.models.Features

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.rober.papelerasvalencia.models.Geometry.GeometrySantaCruz
import com.rober.papelerasvalencia.models.Properties.PropertiesSantaCruz


data class FeaturesSantaCruz(
    val geometrySantaCruz: GeometrySantaCruz,
    val id: Int,
    val propertiesSantaCruz: PropertiesSantaCruz,
    val type: String
) : GeoJsonFeature(
    geometrySantaCruz,
    id.toString(),
    HashMap<String, String>(),
    LatLngBounds(
        LatLng(propertiesSantaCruz.gradY, propertiesSantaCruz.gradX),
        LatLng(propertiesSantaCruz.gradY, propertiesSantaCruz.gradX)
    )
)