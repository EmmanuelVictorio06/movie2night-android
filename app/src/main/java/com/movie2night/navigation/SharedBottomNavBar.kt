package com.movie2night.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

private val NightBlack    = Color(0xFF07070F)
private val NeonPurple    = Color(0xFFB14CFF)
private val MutedLavender = Color(0xFFD0C1D7)

// Rotas que MOSTRAM a bottom nav
val bottomNavRoutes = listOf(
    Routes.Home.route,
    Routes.Matches.route,
    Routes.MyProfile.route,
    Routes.MovieSessions.route  // sessões também mostra a bottom nav
)

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Filmes",  Icons.Default.Movie,    Routes.Home.route),
    BottomNavItem("Matches", Icons.Default.Favorite, Routes.Matches.route),
    BottomNavItem("Perfil",  Icons.Default.Person,   Routes.MyProfile.route)
)

@Composable
fun SharedBottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        color = NightBlack.copy(alpha = 0.95f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, Color.White.copy(alpha = 0.1f)
        ),
        modifier = Modifier.shadow(20.dp, spotColor = Color.Black.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                // Filmes fica ativo tanto em Home quanto em Sessões
                val isActive = when (item.route) {
                    Routes.Home.route -> currentRoute == Routes.Home.route ||
                            currentRoute == Routes.MovieSessions.route
                    else -> currentRoute == item.route
                }

                BottomNavItemView(
                    item = item,
                    active = isActive,
                    onClick = {
                        when (item.route) {
                            Routes.Home.route -> {
                                // Sempre volta para Home limpando o back stack
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(Routes.Home.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                            else -> {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Routes.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    active: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .then(
                if (active) Modifier
                    .background(NeonPurple.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                else Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (active) NeonPurple else MutedLavender.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = item.label,
            fontSize = 11.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            color = if (active) NeonPurple else MutedLavender.copy(alpha = 0.5f)
        )
    }
}