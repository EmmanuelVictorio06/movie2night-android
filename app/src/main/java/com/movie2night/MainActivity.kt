package com.movie2night

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.movie2night.navigation.MovieNavHost
import com.movie2night.presentation.auth.AuthViewModel
import com.movie2night.ui.theme.Movie2NightTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    // Deep link vindo da notificação push: (screen, matchId)
    private var deepLink by mutableStateOf<Pair<String, String>?>(null)

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        requestNotificationPermissionIfNeeded()

        setContent {
            Movie2NightTheme {
                when {
                    authViewModel.isCheckingSession -> SplashLoader()
                    else -> MovieNavHost(
                        isLoggedIn = authViewModel.isLoggedIn,
                        deepLinkScreen = deepLink?.first,
                        deepLinkMatchId = deepLink?.second,
                        onDeepLinkHandled = { deepLink = null }
                    )
                }
            }
        }
    }

    // Chamado quando o app já está aberto e o usuário toca em outra notificação
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val screen = intent?.getStringExtra("screen") ?: return
        val matchId = intent.getStringExtra("matchId") ?: ""
        deepLink = screen to matchId
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun SplashLoader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07070F)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFFB14CFF))
    }
}
