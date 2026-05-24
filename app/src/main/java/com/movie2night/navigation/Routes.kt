package com.movie2night.navigation

sealed class Routes(val route: String) {

    object Login : Routes("login")
    object Register : Routes("register")

    object CreateProfile : Routes("create_profile")

    object Home : Routes("home")

    object MovieSessions : Routes("movies/{movieId}/sessions") {
        fun withId(id: String) = "movies/$id/sessions"
    }

    object InterestedUsers : Routes("interested/{sessionId}") {
        fun withId(id: String) = "interested/$id"
    }

    object Matches : Routes("matches")

    object Chat : Routes("chat/{matchId}") {
        fun withId(id: String) = "chat/$id"
    }

    object CheckIn : Routes("checkin/{sessionId}") {
        fun withId(id: String) = "checkin/$id"
    }

    object Rating : Routes("rating/{matchId}") {
        fun withId(id: String) = "rating/$id"
    }

    object UserProfile : Routes("profile/{userId}") {
        fun withId(id: String) = "profile/$id"
    }
}