package com.SafeWord.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = SafeWordRed,
    secondary = PurpleGrey80,
    onSecondary = SafeWordGreen,
    tertiary = Pink80,
    background = SafeWordGray,
    onBackground = SafeWordBlue
)

// Light color scheme
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = SafeWordRed,
    secondary = PurpleGrey40,
    onSecondary = SafeWordGreen,
    tertiary = Pink40,
    background = SafeWordBlue,
    onBackground = SafeWordGray
)

/**
 * SafewordappTheme - Applies the app's Material 3 theme.
 *
 * @param darkTheme - Boolean to force dark mode. Defaults to system setting.
 * @param dynamicColor - Boolean to enable dynamic theming on Android 12+.
 * @param content - The UI content wrapped inside the theme.
 */
@Composable
fun SafewordappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic color on Android 12+ (S and above)
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
        shapes = Shapes,
        content = content
    )
}
