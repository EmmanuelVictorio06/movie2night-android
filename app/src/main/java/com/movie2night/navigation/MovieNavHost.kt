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
import com.movie2night.presentation.home.HomeScreen
import com.movie2night.presentation.match.InterestedUsersScreen
import com.movie2night.presentation.profile.CreateProfileScreen
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
        composable(Routes.Login.route) {
            LoginScreen(navController)
        }

        composable(Routes.Register.route) {
            RegisterScreen(navController)
        }

        composable(Routes.CreateProfile.route) {
            CreateProfileScreen(navController)
        }

        composable(Routes.Home.route) {
            HomeScreen(navController)
        }

        composable(
            route = Routes.MovieSessions.route,
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: return@composable

            SessionScreen(
                navController = navController,
                movieId = movieId
            )
        }

        composable(
            route = Routes.InterestedUsers.route,
            arguments = listOf(
                navArgument("sessionId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable

            InterestedUsersScreen(
                navController = navController,
                sessionId = sessionId
            )
        }
    }
}