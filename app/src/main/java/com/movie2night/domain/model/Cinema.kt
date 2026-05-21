package com.movie2night.domain.model

/**
 * Cinema cadastrado no sistema.
 * A localização é armazenada como coordenadas — nunca o endereço exato do usuário.
 */
data class Cinema(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val city: String
)
