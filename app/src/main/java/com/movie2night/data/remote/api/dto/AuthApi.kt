package com.movie2night.data.remote.api

import com.movie2night.data.remote.dto.AuthResponse
import com.movie2night.data.remote.dto.LoginRequest
import com.movie2night.data.remote.dto.RegisterRequest
import com.movie2night.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("auth/me")
    suspend fun getLoggedUser(): UserDto
}
