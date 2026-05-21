package com.movie2night.data.remote.dto

import com.movie2night.domain.model.Movie

data class MovieDto(
    val id: String,
    val title: String,
    val synopsis: String,
    val posterUrl: String,
    val genre: String,
    val durationMinutes: Int,
    val rating: String,
    val imdbScore: Float?
) {
    fun toDomain() = Movie(
        id = id,
        title = title,
        synopsis = synopsis,
        posterUrl = posterUrl,
        genre = genre,
        durationMinutes = durationMinutes,
        rating = rating,
        imdbScore = imdbScore
    )
}

