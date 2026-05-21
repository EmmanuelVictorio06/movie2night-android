package com.movie2night.data.remote.dto

import com.movie2night.domain.model.Cinema
import com.movie2night.domain.model.Session

data class CinemaDto(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val city: String
) {
    fun toDomain() = Cinema(id, name, address, latitude, longitude, city)
}

data class SessionDto(
    val id: String,
    val movie: MovieDto,
    val cinema: CinemaDto,
    val dateTime: String,
    val room: String?,
    val availableSeats: Int?,
    val interestedCount: Int
) {
    fun toDomain() = Session(
        id = id,
        movie = movie.toDomain(),
        cinema = cinema.toDomain(),
        dateTime = dateTime,
        room = room,
        availableSeats = availableSeats,
        interestedCount = interestedCount
    )
}

