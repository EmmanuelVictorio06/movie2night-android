package com.movie2night.data.remote.api

import com.movie2night.data.remote.dto.MovieDto
import com.movie2night.data.remote.dto.SessionDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("movies")
    suspend fun getMovies(): List<MovieDto>

    @GET("movies/{movieId}/sessions")
    suspend fun getSessionsByMovie(@Path("movieId") movieId: String): List<SessionDto>

    @GET("sessions")
    suspend fun getSessionsByCity(@Query("city") city: String): List<SessionDto>

    @GET("sessions/{sessionId}")
    suspend fun getSessionById(@Path("sessionId") sessionId: String): SessionDto
}
