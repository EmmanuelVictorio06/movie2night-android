package com.movie2night.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.movie2night.presentation.auth.LoginScreen
import com.movie2night.presentation.auth.RegisterScreen
import com.movie2night.presentation.chat.ChatScreen
import com.movie2night.presentation.home.HomeScreen
import com.movie2night.presentation.match.InterestedUsersScreen
import com.movie2night.presentation.matches.MatchesScreen
import com.movie2night.presentation.profile.CreateProfileScreen
import com.movie2night.presentation.profile.UserProfileScreen
import com.movie2night.presentation.session.SessionScreen

@Composable
fun MovieNavHost(
    isLoggedIn: Boolean,
    navController: NavHostController = rememberNavController()
) {
    val start = if (isLoggedIn) Routes.Home.route else Routes.Login.route

    NavHost(navController = navController, startDestination = start) {

        composable(Routes.Login.route) { LoginScreen(navController) }
        composable(Routes.Register.route) { RegisterScreen(navController) }
        composable(Routes.CreateProfile.route) { CreateProfileScreen(navController) }
        composable(Routes.Home.route) { HomeScreen(navController) }

        // ── Sessões do filme ──────────────────────────────────
        composable(
            route = Routes.MovieSessions.route,
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { back ->
            val movieId = back.arguments?.getString("movieId") ?: return@composable
            SessionScreen(navController = navController, movieId = movieId)
        }

        // ── Interessados na sessão ────────────────────────────
        composable(
            route = Routes.InterestedUsers.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) { back ->
            val sessionId = back.arguments?.getString("sessionId") ?: return@composable
            InterestedUsersScreen(navController = navController, sessionId = sessionId)
        }

        // ── Lista de matches (recebidos + aceitos + enviados) ─
        composable(Routes.Matches.route) {
            MatchesScreen(navController = navController)
        }

        // ── Chat ──────────────────────────────────────────────
        composable(
            route = Routes.Chat.route,
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { back ->
            val matchId = back.arguments?.getString("matchId") ?: return@composable
            ChatScreen(
                navController = navController,
                matchId = matchId,
                currentUserId = ""  // corrigido via ChatViewModel lendo AuthDataStore
            )
        }

        // ── Perfil público ────────────────────────────────────
        composable(
            route = Routes.UserProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { back ->
            val userId = back.arguments?.getString("userId") ?: return@composable
            UserProfileScreen(navController = navController, userId = userId)
        }
    }
}