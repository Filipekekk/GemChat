package com.gemchat.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GemChatColorScheme = darkColorScheme(
    primary          = Primary,
    onPrimary        = Color(0xFF330099),
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary        = Color(0xFFBAC3FF),
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = Color(0xFFA8B4FF),
    tertiary         = Tertiary,
    background       = SurfaceContainerLowest,
    surface          = Surface,
    onSurface        = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceVariant   = SurfaceContainerHighest,
    outline          = Color(0xFF938EA0),
    outlineVariant   = OutlineVariant,
    error            = Error,
)

@Composable
fun GemChatTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GemChatColorScheme,
        content = content
    )
}