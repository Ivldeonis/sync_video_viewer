package com.example.syncvideoviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.syncvideoviewer.ui.screens.HomeScreen
import com.example.syncvideoviewer.ui.screens.RoomScreen
import com.example.syncvideoviewer.ui.theme.SyncVideoViewerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SyncVideoViewerTheme {
                SyncVideoViewerApp()
            }
        }

        // Handle deep links
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: android.content.Intent) {
        val data = intent.data
        if (data != null) {
            val roomId = when {
                data.scheme == "syncvideoviewer" -> data.pathSegments.getOrNull(0)
                data.path?.startsWith("/room/") == true -> data.path?.removePrefix("/room/")
                else -> null
            }
            // Pass roomId to compose navigation if needed
        }
    }
}

@Composable
fun SyncVideoViewerApp() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                HomeScreen(
                    onRoomClick = { roomId ->
                        navController.navigate("room/$roomId")
                    },
                    onCreateRoom = {
                        // Navigate to create room screen if needed
                    }
                )
            }

            composable(
                route = "room/{roomId}",
                arguments = listOf(
                    navArgument("roomId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
                RoomScreen(
                    roomId = roomId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
