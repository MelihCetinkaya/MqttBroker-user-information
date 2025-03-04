package com.example.heatandhumidity.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<Unit>
}

data class RegisterRequest(
    val name: String,
    val email: String,
    val username: String,
    val password: String
)
