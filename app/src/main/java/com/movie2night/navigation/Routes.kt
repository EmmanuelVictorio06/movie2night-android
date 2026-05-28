package com.movie2night.navigation

sealed class Routes(val route: String) {
    object Login         : Routes("login")
    object Register      : Routes("register")
    object CreateProfile : Routes("create_profile")
    object Home          : Routes("home")
    object Matches       : Routes("matches")
    object MyProfile     : Routes("my_profile")

    object MovieSessions : Routes("movies/{movieId}/sessions") {
        fun withId(id: String) = "movies/$id/sessions"
    }
    object InterestedUsers : Routes("interested/{sessionId}") {
        fun withId(id: String) = "interested/$id"
    }
    object Chat : Routes("chat/{matchId}") {
        fun withId(id: String) = "chat/$id"
    }
    object UserProfile : Routes("profile/{userId}") {
        fun withId(id: String) = "profile/$id"
    }

    object CheckIn : Routes("checkin/{sessionId}/{matchId}/{ratedUserId}") {
        fun build(sessionId: String, matchId: String, ratedUserId: String) =
            "checkin/$sessionId/$matchId/$ratedUserId"
    }
    object Rating : Routes("rating/{matchId}/{ratedUserId}") {
        fun build(matchId: String, ratedUserId: String) =
            "rating/$matchId/$ratedUserId"
    }
}