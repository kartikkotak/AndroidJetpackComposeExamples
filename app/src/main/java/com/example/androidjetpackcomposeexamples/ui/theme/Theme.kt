package com.example.androidjetpackcomposeexamples.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.androidjetpackcomposeexamples.data.*

fun AppSettings.useDarkTheme(systemDark: Boolean): Boolean = when (appearance) {
    Appearance.System -> systemDark
    Appearance.Light -> false
    Appearance.Dark -> true
}

internal fun paletteColors(palette: AppPalette, dark: Boolean): ColorScheme {
    val (lightPrimary, darkPrimary) = when (palette) {
        AppPalette.Default -> Purple40 to Purple80
        AppPalette.Ocean -> Color(0xFF00639B) to Color(0xFF96CCFF)
        AppPalette.Forest -> Color(0xFF246B38) to Color(0xFF98D5A1)
        AppPalette.Purple -> Color(0xFF8434A2) to Color(0xFFE9B3FF)
    }
    val primary = if (dark) darkPrimary else lightPrimary
    val container = lerp(lightPrimary, if (dark) Color.Black else Color.White, if (dark) 0.4f else 0.85f)
    val surface = lerp(lightPrimary, if (dark) Color.Black else Color.White, if (dark) 0.87f else 0.98f)
    val onSurface = if (dark) Color(0xFFE7E2E6) else Color(0xFF1D1B20)
    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = primary, onPrimary = if (dark) Color(0xFF17121C) else Color.White,
        primaryContainer = container, onPrimaryContainer = if (dark) Color.White else Color(0xFF17121C),
        secondary = primary, onSecondary = if (dark) Color.Black else Color.White,
        secondaryContainer = container, onSecondaryContainer = if (dark) Color.White else Color(0xFF17121C),
        tertiary = primary, onTertiary = if (dark) Color.Black else Color.White,
        tertiaryContainer = container, onTertiaryContainer = if (dark) Color.White else Color(0xFF17121C),
        background = surface, onBackground = onSurface, surface = surface, onSurface = onSurface,
        surfaceTint = primary,
        surfaceDim = lerp(surface, Color.Black, 0.08f),
        surfaceBright = lerp(surface, Color.White, if (dark) 0.12f else 0.01f),
        surfaceContainerLowest = surface,
        surfaceContainerLow = lerp(surface, primary, 0.03f),
        surfaceContainer = lerp(surface, primary, 0.06f),
        surfaceContainerHigh = lerp(surface, primary, 0.09f),
        surfaceContainerHighest = lerp(surface, primary, 0.12f),
    )
}

@Composable
fun AndroidJetpackComposeExamplesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    settings: AppSettings = AppSettings(dynamicColors = dynamicColor),
    content: @Composable () -> Unit,
) {
    val dark = settings.useDarkTheme(darkTheme)
    val context = LocalContext.current
    val colors = when {
        settings.dynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        else -> paletteColors(settings.palette, dark)
    }
    val typography = remember(settings.font) {
        if (settings.font == AppFont.Default) Typography else Typography.copy(
            displayLarge = Typography.displayLarge.copy(fontFamily = FontFamily.Serif),
            displayMedium = Typography.displayMedium.copy(fontFamily = FontFamily.Serif),
            displaySmall = Typography.displaySmall.copy(fontFamily = FontFamily.Serif),
            headlineLarge = Typography.headlineLarge.copy(fontFamily = FontFamily.Serif),
            headlineMedium = Typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
            headlineSmall = Typography.headlineSmall.copy(fontFamily = FontFamily.Serif),
            titleLarge = Typography.titleLarge.copy(fontFamily = FontFamily.Serif),
            titleMedium = Typography.titleMedium.copy(fontFamily = FontFamily.Serif),
            titleSmall = Typography.titleSmall.copy(fontFamily = FontFamily.Serif),
            bodyLarge = Typography.bodyLarge.copy(fontFamily = FontFamily.Serif),
            bodyMedium = Typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
            bodySmall = Typography.bodySmall.copy(fontFamily = FontFamily.Serif),
            labelLarge = Typography.labelLarge.copy(fontFamily = FontFamily.Serif),
            labelMedium = Typography.labelMedium.copy(fontFamily = FontFamily.Serif),
            labelSmall = Typography.labelSmall.copy(fontFamily = FontFamily.Serif),
        )
    }
    val radius = when (settings.shape) { AppShape.Rounded -> 16; AppShape.SlightlyRounded -> 4; AppShape.Square -> 0 }
    val shapes = Shapes(
        extraSmall = RoundedCornerShape((radius / 2).dp), small = RoundedCornerShape(radius.dp),
        medium = RoundedCornerShape(radius.dp), large = RoundedCornerShape((radius * 1.5f).dp),
        extraLarge = RoundedCornerShape((radius * 2).dp),
    )
    MaterialTheme(colorScheme = colors, typography = typography, shapes = shapes, content = content)
}
