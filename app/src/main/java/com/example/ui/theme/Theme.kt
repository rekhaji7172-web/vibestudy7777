package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CosmicSlateScheme = darkColorScheme(
    primary = SlatePrimary,
    secondary = SlateSecondary,
    tertiary = SlateAccent,
    background = SlateBackground,
    surface = SlateSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    primaryContainer = Color(0xFF1E284E),
    onPrimaryContainer = Color(0xFFBAC7FF)
)

private val TealZenScheme = darkColorScheme(
    primary = TealPrimary,
    secondary = TealSecondary,
    tertiary = TealAccent,
    background = TealBackground,
    surface = TealSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color(0xFFECFDFC),
    onSurface = Color(0xFFECFDFC),
    primaryContainer = Color(0xFF0F362A),
    onPrimaryContainer = Color(0xFFA3FBE3)
)

private val CyberVioletScheme = darkColorScheme(
    primary = CyberPrimary,
    secondary = CyberSecondary,
    tertiary = CyberAccent,
    background = CyberBackground,
    surface = CyberSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFFCF0F6),
    onSurface = Color(0xFFFCF0F6),
    primaryContainer = Color(0xFF32084E),
    onPrimaryContainer = Color(0xFFFFBDE2)
)

private val AmberOasisScheme = darkColorScheme(
    primary = AmberPrimary,
    secondary = AmberSecondary,
    tertiary = AmberAccent,
    background = AmberBackground,
    surface = AmberSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color(0xFFFFFDF5),
    onSurface = Color(0xFFFFFDF5),
    primaryContainer = Color(0xFF381C06),
    onPrimaryContainer = Color(0xFFFFE0B2)
)

@Composable
fun VibeStudyTheme(
    selectedThemeIndex: Int = 0,
    content: @Composable () -> Unit
) {
    val colorScheme = when (selectedThemeIndex) {
        0 -> CosmicSlateScheme
        1 -> TealZenScheme
        2 -> CyberVioletScheme
        3 -> AmberOasisScheme
        else -> CosmicSlateScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
