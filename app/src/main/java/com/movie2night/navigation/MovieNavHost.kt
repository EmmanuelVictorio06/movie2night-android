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
import com.movie2night.presentation.profile.CreateProfileScreen
import com.movie2night.presentation.profile.EditProfileScreen
import com.movie2night.presentation.profile.UserProfileScreen

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

        // ── Auth ────────────────────────────────────────────
        composable(Routes.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Routes.Register.route) {
            RegisterScreen(navController = navController)
        }

        // ── Perfil inicial (onboarding) ─────────────────────
        composable(Routes.CreateProfile.route) {
            CreateProfileScreen(navController = navController)
        }

        // ── Principal ───────────────────────────────────────
        composable(Routes.Home.route) {
            HomeScreen(navController = navController)
        }

        // ── Sessão ──────────────────────────────────────────
        composable(
            route = Routes.SessionDetail.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            InterestedUsersScreen(navController = navController, sessionId = sessionId)
        }

        // ── Interessados na sessão ──────────────────────────
        composable(
            route = Routes.InterestedUsers.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
            InterestedUsersScreen(navController = navController, sessionId = sessionId)
        }

        // ── Chat ────────────────────────────────────────────
        composable(
            route = Routes.Chat.route,
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: return@composable
            ChatScreen(
                navController = navController,
                matchId = matchId,
                currentUserId = ""   // substituir pelo ID do usuário logado futuramente
            )
        }

        // ── Perfil de outro usuário ─────────────────────────
        composable(
            route = Routes.UserProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            UserProfileScreen(navController = navController, userId = userId)
        }

        // ── Editar perfil próprio ───────────────────────────
        composable(
            route = "edit_profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            EditProfileScreen(navController = navController, userId = userId)
        }
    }
}