package com.movie2night.domain.model

/**
 * Resultado de uma tentativa de check-in no cinema.
 * O backend é a fonte da verdade da distância (validação de 500m).
 */
data class CheckInResult(
    val success: Boolean,
    val distanceMeters: Int,
    val message: String,
    val ratingUnlocked: Boolean
)
