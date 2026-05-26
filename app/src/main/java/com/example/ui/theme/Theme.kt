package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkSchoolPrimary,
    secondary = DarkSchoolSecondary,
    tertiary = DarkSchoolTertiary,
    background = DarkSchoolBackground,
    surface = DarkSchoolSurface,
    onPrimary = DarkSchoolOnPrimary,
    onSecondary = DarkSchoolOnSecondary,
    onBackground = DarkSchoolOnBackground,
    onSurface = DarkSchoolOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = SchoolPrimary,
    secondary = SchoolSecondary,
    tertiary = SchoolTertiary,
    background = SchoolBackground,
    surface = SchoolSurface,
    onPrimary = SchoolOnPrimary,
    onSecondary = SchoolOnSecondary,
    onBackground = SchoolOnBackground,
    onSurface = SchoolOnSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Turn dynamicColor as false by default to showcase Hirawati School identity colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

