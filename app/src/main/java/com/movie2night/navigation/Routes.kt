package com.movie2night.navigation

/**
 * Rotas de navegação do app.
 * Centralizar aqui evita strings espalhadas pelo código.
 */
sealed class Routes(val route: String) {

    // Auth
    object Login       : Routes("login")
    object Register    : Routes("register")

    // Perfil
    object CreateProfile : Routes("create_profile")

    // Principal
    object Home        : Routes("home")
    object SessionDetail : Routes("session/{sessionId}") {
        fun withId(id: String) = "session/$id"
    }

    // Match
    object InterestedUsers : Routes("interested/{sessionId}") {
        fun withId(id: String) = "interested/$id"
    }
    object Matches     : Routes("matches")

    // Chat
    object Chat : Routes("chat/{matchId}") {
        fun withId(id: String) = "chat/$id"
    }

    // Check-in e avaliação
    object CheckIn : Routes("checkin/{sessionId}") {
        fun withId(id: String) = "checkin/$id"
    }
    object Rating  : Routes("rating/{matchId}") {
        fun withId(id: String) = "rating/$id"
    }

    // Perfil do outro usuário
    object UserProfile : Routes("profile/{userId}") {
        fun withId(id: String) = "profile/$id"
    }
}
