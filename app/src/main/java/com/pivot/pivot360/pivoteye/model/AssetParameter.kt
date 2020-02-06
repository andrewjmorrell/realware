package com.pivot.pivot360.model

import com.google.gson.annotations.SerializedName

data class AssetParameter(
        @SerializedName("age")
        val age: String = "",
        @SerializedName("barcode")
        val barcode: String = "",
        @SerializedName("make")
        val make: String = "",
        @SerializedName("model")
        val model: String = "",
        @SerializedName("serial_no")
        val serialNo: String = "",
        @SerializedName("type")
        val type: String = ""
)