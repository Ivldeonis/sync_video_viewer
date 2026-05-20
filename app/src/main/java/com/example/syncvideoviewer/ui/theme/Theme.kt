package com.example.syncvideoviewer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE50914),      // Netflix Red
    onPrimary = Color.White,
    primaryContainer = Color(0xFF831010),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = Color(0xFF221A1F),    // Dark Gray
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF403638),
    onSecondaryContainer = Color(0xFFF7E7EC),
    tertiary = Color(0xFF141414),     // Almost Black
    onTertiary = Color.White,
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFF141414),   // Netflix Dark Background
    onBackground = Color(0xFFEBE9EC),
    surface = Color(0xFF221A1F),
    onSurface = Color(0xFFEBE9EC)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE50914),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410E0B),
    secondary = Color(0xFF625B60),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEBDDE2),
    onSecondaryContainer = Color(0xFF221A1F),
    tertiary = Color(0xFF7F5F7B),
    onTertiary = Color.White,
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFEFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFEFBFE),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun SyncVideoViewerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)?.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
