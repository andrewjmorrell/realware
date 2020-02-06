package com.pivot.pivot360.model

import com.google.gson.annotations.SerializedName

data class PaeAssetParameter(


        @SerializedName("buno")
        val buno: String = "",
        @SerializedName("ecd")
        val ecd: String = "",
        @SerializedName("jammer")
        val jammer: String = "",
        @SerializedName("last_flown")
        val lastFlown: String = "",
        @SerializedName("ln-260")
        val ln260: String = "",
        @SerializedName("mc_status")
        val mcStatus: String = "",
        @SerializedName("modex")
        val modex: String = "",
        @SerializedName("radar_type")
        val radarType: String = "",
        @SerializedName("status_notes")
        val statusNotes: String = "",
        @SerializedName("type")
        val type: String = ""
)