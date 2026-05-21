package com.movie2night.domain.model

/**
 * Representa um filme em cartaz.
 */
data class Movie(
    val id: String,
    val title: String,
    val synopsis: String,
    val posterUrl: String,
    val genre: String,
    val durationMinutes: Int,
    val rating: String,         // ex: "14 anos", "Livre"
    val imdbScore: Float?       // pode vir nulo se não integrado ao IMDB
)
