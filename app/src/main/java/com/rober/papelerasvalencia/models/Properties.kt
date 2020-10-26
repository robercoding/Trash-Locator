package com.rober.papelerasvalencia.models


import com.google.gson.annotations.SerializedName

data class Properties(
    @SerializedName("CVIA")
    val cVIA: String,
    @SerializedName("CVIA_1")
    val cVIA1: String,
    @SerializedName("DIRECCION")
    val direction: String,
    @SerializedName("GRAD_X")
    val gradX: Double,
    @SerializedName("GRAD_Y")
    val gradY: Double,
    @SerializedName("TEXTO")
    val text: String,
    @SerializedName("TIPO")
    val type: String,
    @SerializedName("UTM_X")
    val utmX: Double,
    @SerializedName("UTM_Y")
    val utmY: Double
)