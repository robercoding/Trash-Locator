package com.rober.papelerasvalencia.models

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.data.geojson.GeoJsonFeature


data class FeaturesSantaCruz(
    val geometry: Geometry,
    val id: Int,
    val properties: Properties,
    val type: String
) : GeoJsonFeature(geometry, id.toString(), HashMap<String, String>(), LatLngBounds(LatLng(properties.gradY, properties.gradX), LatLng(properties.gradY, properties.gradX)))