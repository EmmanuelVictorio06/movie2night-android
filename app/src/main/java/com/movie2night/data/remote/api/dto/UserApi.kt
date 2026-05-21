package com.movie2night.data.remote.api

import com.movie2night.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class UpdateProfileRequest(val name: String, val bio: String?, val photoUrl: String?)
data class RatingRequest(val ratedUserId: String, val score: Int, val comment: String?)
data class ReportRequest(val reportedUserId: String, val reason: String, val description: String?)
data class BlockRequest(val blockedUserId: String)

interface UserApi {
    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): UserDto

    @PATCH("users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UserDto

    @POST("ratings")
    suspend fun rateUser(@Body request: RatingRequest)

    @POST("reports")
    suspend fun reportUser(@Body request: ReportRequest)

    @POST("blocks")
    suspend fun blockUser(@Body request: BlockRequest)
}

