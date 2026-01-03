package com.dockflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NeonColorScheme = darkColorScheme(
    primary = NeonPrimary,
    secondary = NeonSecondary,
    tertiary = NeonAccent,
    background = NeonBackground,
    surface = NeonSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val MinimalColorScheme = lightColorScheme(
    primary = MinimalPrimary,
    secondary = MinimalSecondary,
    tertiary = MinimalAccent,
    background = MinimalBackground,
    surface = MinimalSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

private val AnalogColorScheme = darkColorScheme(
    primary = AnalogPrimary,
    secondary = AnalogSecondary,
    tertiary = AnalogAccent,
    background = AnalogBackground,
    surface = AnalogSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = AnalogAccent,
    onSurface = AnalogAccent,
)

@Composable
fun DockFlowTheme(
    themeType: ThemeType = ThemeType.NEON,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        ThemeType.NEON -> NeonColorScheme
        ThemeType.MINIMAL -> MinimalColorScheme
        ThemeType.ANALOG -> AnalogColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
