package com.rober.papelerasvalencia.models.Properties


import com.google.gson.annotations.SerializedName

data class PropertiesWashingtonDC(
    @SerializedName("BUSSTOP")
    val bUSSTOP: Int,
    @SerializedName("CREATEDAT")
    val cREATEDAT: String,
    @SerializedName("LINK_ID")
    val lINKID: Int,
    @SerializedName("LITTERID")
    val lITTERID: Int,
    @SerializedName("LITTERID_1")
    val lITTERID1: Int,
    @SerializedName("MAPX")
    val mAPX: Double,
    @SerializedName("MAPY")
    val mAPY: Double,
    @SerializedName("NAME")
    val nAME: String,
    @SerializedName("OBJECTID")
    val oBJECTID: Int,
    @SerializedName("TYPE")
    val tYPE: String
)