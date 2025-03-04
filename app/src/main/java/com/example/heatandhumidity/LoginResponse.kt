package com.example.heatandhumidity

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("value") val value: Boolean
) 