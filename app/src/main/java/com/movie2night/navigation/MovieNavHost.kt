package com.movie2night.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.movie2night.presentation.auth.LoginScreen
import com.movie2night.presentation.auth.RegisterScreen
import com.movie2night.presentation.chat.ChatScreen
import com.movie2night.presentation.home.HomeScreen
import com.movie2night.presentation.match.InterestedUsersScreen
import com.movie2night.presentation.matches.MatchesScreen
import com.movie2night.presentation.profile.CreateProfileScreen
import com.movie2night.presentation.profile.MyProfileScreen
import com.movie2night.presentation.profile.UserProfileScreen
import com.movie2night.presentation.session.SessionScreen

@Composable
fun MovieNavHost(
    isLoggedIn: Boolean,
    navController: NavHostController = rememberNavController()
) {
    val start = if (isLoggedIn) Routes.Home.route else Routes.Login.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Bottom nav aparece nas rotas principais + sessões
    val showBottomNav = bottomNavRoutes.any { bottomRoute ->
        currentRoute == bottomRoute ||
                // Sessões tem parâmetro dinâmico — compara o template da rota
                (bottomRoute == Routes.MovieSessions.route &&
                        currentRoute?.startsWith("movies/") == true)
    }

    Scaffold(
        containerColor = Color(0xFF07070F),
        bottomBar = {
            if (showBottomNav) {
                SharedBottomNavBar(navController = navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = start,
            modifier = Modifier.padding(padding)
        ) {
            // ── Sem bottom nav ──────────────────────────────
            composable(Routes.Login.route)         { LoginScreen(navController) }
            composable(Routes.Register.route)      { RegisterScreen(navController) }
            composable(Routes.CreateProfile.route) { CreateProfileScreen(navController) }

            // ── Com bottom nav ──────────────────────────────
            composable(Routes.Home.route)      { HomeScreen(navController) }
            composable(Routes.Matches.route)   { MatchesScreen(navController) }
            composable(Routes.MyProfile.route) { MyProfileScreen(navController) }

            composable(
                route = Routes.MovieSessions.route,
                arguments = listOf(navArgument("movieId") { type = NavType.StringType })
            ) { back ->
                SessionScreen(navController, back.arguments?.getString("movieId") ?: "")
            }

            // ── Sem bottom nav ──────────────────────────────
            composable(
                route = Routes.InterestedUsers.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
            ) { back ->
                InterestedUsersScreen(navController, back.arguments?.getString("sessionId") ?: "")
            }

            composable(
                route = Routes.Chat.route,
                arguments = listOf(navArgument("matchId") { type = NavType.StringType })
            ) { back ->
                ChatScreen(navController, back.arguments?.getString("matchId") ?: "")
            }

            composable(
                route = Routes.UserProfile.route,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { back ->
                UserProfileScreen(navController, back.arguments?.getString("userId") ?: "")
            }
        }
    }
}