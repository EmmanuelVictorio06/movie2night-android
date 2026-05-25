package com.movie2night.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.movie2night.presentation.profile.CreateProfileScreen
import com.movie2night.presentation.profile.UserProfileScreen
import com.movie2night.presentation.session.SessionScreen

@Composable
fun MovieNavHost(
    isLoggedIn: Boolean,
    navController: NavHostController = rememberNavController()
) {
    val start = if (isLoggedIn) Routes.Home.route else Routes.Login.route

    NavHost(
        navController = navController,
        startDestination = start
    ) {

        // ── Auth ────────────────────────────────────────────────
        composable(Routes.Login.route) {
            LoginScreen(navController)
        }

        composable(Routes.Register.route) {
            RegisterScreen(navController)
        }

        // ── Onboarding ──────────────────────────────────────────
        composable(Routes.CreateProfile.route) {
            CreateProfileScreen(navController)
        }

        // ── Home ────────────────────────────────────────────────
        composable(Routes.Home.route) {
            HomeScreen(navController)
        }

        // ── Sessões do filme ────────────────────────────────────
        composable(
            route = Routes.MovieSessions.route,
            arguments = listOf(
                navArgument("movieId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")
                ?: return@composable
            SessionScreen(
                navController = navController,
                movieId = movieId
            )
        }

        // ── Interessados na sessão ──────────────────────────────
        composable(
            route = Routes.InterestedUsers.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId")
                ?: return@composable
            InterestedUsersScreen(
                navController = navController,
                sessionId = sessionId
            )
        }

        // ── Chat ────────────────────────────────────────────────
        composable(
            route = Routes.Chat.route,
            arguments = listOf(
                navArgument("matchId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId")
                ?: return@composable
            ChatScreen(
                navController = navController,
                matchId = matchId,
                currentUserId = "" // será corrigido na próxima etapa com AuthDataStore
            )
        }

        // ── Perfil público de outro usuário ─────────────────────
        composable(
            route = Routes.UserProfile.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
                ?: return@composable
            UserProfileScreen(
                navController = navController,
                userId = userId
            )
        }
    }
}