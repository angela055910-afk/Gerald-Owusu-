package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val JungleDarkColorScheme = darkColorScheme(
    primary = JunglePrimary,
    onPrimary = Color(0xFF00382B),
    primaryContainer = JungleDeepForest,
    onPrimaryContainer = Color(0xFF79F8D0),
    secondary = JungleSecondary,
    onSecondary = Color(0xFF00391A),
    secondaryContainer = Color(0xFF005328),
    onSecondaryContainer = Color(0xFF6CFF9B),
    tertiary = EchoStreamTeal,
    onTertiary = Color(0xFF003730),
    background = JungleDarkBackground,
    onBackground = Color(0xFFE9EDEF),
    surface = JungleDarkSurface,
    onSurface = Color(0xFFE9EDEF),
    surfaceVariant = JungleDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF8696A0),
    outline = Color(0xFF2A3942)
)

private val JungleLightColorScheme = lightColorScheme(
    primary = JunglePrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF79F8D0),
    onPrimaryContainer = Color(0xFF002018),
    secondary = JungleSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF6CFF9B),
    onSecondaryContainer = Color(0xFF00210E),
    tertiary = JunglePrimary,
    onTertiary = Color.White,
    background = JungleLightBackground,
    onBackground = Color(0xFF111B21),
    surface = JungleLightSurface,
    onSurface = Color(0xFF111B21),
    surfaceVariant = JungleLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF667781),
    outline = Color(0xFFD1D7DB)
)

@Composable
fun JungleTheme(
    darkTheme: Boolean = true, // WhatsApp / Jungle messaging defaults to sleek dark mode
    dynamicColor: Boolean = false, // Keep brand emerald & deep teal colors for true Jungle feel
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> JungleDarkColorScheme
        else -> JungleLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) = JungleTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
