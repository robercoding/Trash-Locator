package com.rober.papelerasvalencia.models.Features

import com.rober.papelerasvalencia.models.Geometry.GeometryWashingtonDC
import com.rober.papelerasvalencia.models.Properties.PropertiesWashingtonDC


data class FeatureWashingtonDC(
    val geometry: GeometryWashingtonDC,
    val properties: PropertiesWashingtonDC,
    val type: String
)