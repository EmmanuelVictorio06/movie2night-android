package com.movie2night.domain.usecase

import com.movie2night.domain.model.Movie
import com.movie2night.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(): Flow<List<Movie>> {
        return movieRepository.getMovies()
    }
}
