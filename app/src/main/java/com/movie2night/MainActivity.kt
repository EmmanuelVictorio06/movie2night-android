package com.movie2night

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.movie2night.navigation.MovieNavHost
import com.movie2night.presentation.auth.AuthViewModel
import com.movie2night.ui.theme.Movie2NightTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Movie2NightTheme {
                val isLoggedIn by remember { mutableStateOf(authViewModel.isLoggedIn) }
                MovieNavHost(isLoggedIn = isLoggedIn)
            }
        }
    }
}