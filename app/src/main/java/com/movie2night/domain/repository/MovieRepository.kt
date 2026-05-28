package com.movie2night.domain.repository

import com.movie2night.domain.model.CheckInResult
import com.movie2night.domain.model.Movie
import com.movie2night.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(): Flow<List<Movie>>

    suspend fun getSessionsByMovie(movieId: String): List<Session>

    suspend fun getSessionsByCity(city: String): List<Session>

    suspend fun getSessionById(sessionId: String): Session?

    suspend fun checkIn(
        sessionId: String,
        latitude: Double,
        longitude: Double
    ): Result<CheckInResult>
}

