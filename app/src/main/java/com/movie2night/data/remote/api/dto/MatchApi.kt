package com.movie2night.data.remote.api

import com.movie2night.data.remote.dto.MatchDto
import com.movie2night.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class InterestRequest(val sessionId: String)
data class MatchRequest(val receiverId: String, val sessionId: String)
data class MatchResponse(val matchId: String, val accept: Boolean)

interface MatchApi {
    @POST("interests")
    suspend fun registerInterest(@Body request: InterestRequest): Unit

    @GET("sessions/{sessionId}/interested")
    suspend fun getInterestedUsers(@Path("sessionId") sessionId: String): List<UserDto>

    @POST("matches")
    suspend fun sendMatchRequest(@Body request: MatchRequest): MatchDto

    @PATCH("matches/{matchId}/respond")
    suspend fun respondToMatch(
        @Path("matchId") matchId: String,
        @Body response: MatchResponse
    ): MatchDto

    @GET("matches/me")
    suspend fun getMyMatches(): List<MatchDto>

    @GET("matches/{matchId}")
    suspend fun getMatchById(@Path("matchId") matchId: String): MatchDto
}