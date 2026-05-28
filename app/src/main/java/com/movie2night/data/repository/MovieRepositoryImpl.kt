package com.movie2night.data.repository

import com.movie2night.data.remote.api.MovieApi
import com.movie2night.data.remote.dto.CheckInRequest
import com.movie2night.domain.model.CheckInResult
import com.movie2night.domain.model.Movie
import com.movie2night.domain.model.Session
import com.movie2night.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepositoryImpl(
    private val movieApi: MovieApi
) : MovieRepository {

    override fun getMovies(): Flow<List<Movie>> = flow {
        val movies = movieApi.getMovies().map { it.toDomain() }
        emit(movies)
    }

    override suspend fun getSessionsByMovie(movieId: String): List<Session> {
        return movieApi.getSessionsByMovie(movieId).map { it.toDomain() }
    }

    override suspend fun getSessionsByCity(city: String): List<Session> {
        return movieApi.getSessionsByCity(city).map { it.toDomain() }
    }

    override suspend fun getSessionById(sessionId: String): Session? = runCatching {
        movieApi.getSessionById(sessionId).toDomain()
    }.getOrNull()

    override suspend fun checkIn(
        sessionId: String,
        latitude: Double,
        longitude: Double
    ): Result<CheckInResult> = runCatching {
        movieApi.checkIn(sessionId, CheckInRequest(latitude, longitude)).toDomain()
    }
}
