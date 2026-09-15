package com.infy.browser.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val InfyDarkColorScheme = darkColorScheme(
    primary = InfyNeonBlue,
    secondary = InfyNeonPurple,
    tertiary = InfyNeonCyan,
    background = InfyBlack,
    surface = InfySurface,
    surfaceVariant = InfySurfaceAlt,
    onPrimary = InfyBlack,
    onSecondary = InfyBlack,
    onBackground = InfyTextPrimary,
    onSurface = InfyTextPrimary,
    error = InfyError
)

@Composable
fun InfyBrowserTheme(
    // The browser is dark-by-design (space aesthetic); system light mode
    // is intentionally not offered as a separate theme.
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = InfyDarkColorScheme,
        typography = InfyTypography,
        content = content
    )
}
