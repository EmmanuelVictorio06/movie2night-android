package com.movie2night.data.remote.api

import com.movie2night.data.remote.dto.MessageDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class SendMessageRequest(val content: String)

interface ChatApi {
    @GET("matches/{matchId}/messages")
    suspend fun getMessages(@Path("matchId") matchId: String): List<MessageDto>

    @POST("matches/{matchId}/messages")
    suspend fun sendMessage(
        @Path("matchId") matchId: String,
        @Body request: SendMessageRequest
    ): MessageDto

    @PATCH("matches/{matchId}/read")
    suspend fun markAsRead(@Path("matchId") matchId: String)
}