package com.movie2night.domain.model

/**
 * Uma sessão específica de um filme em um cinema.
 * É a unidade central do app — usuários se conectam por sessão.
 */
data class Session(
    val id: String,
    val movie: Movie,
    val cinema: Cinema,
    val dateTime: String,           // formato: "2025-06-15T19:30:00"
    val room: String?,              // número ou nome da sala (opcional)
    val availableSeats: Int?,
    val interestedCount: Int        // quantas pessoas marcaram interesse
)
