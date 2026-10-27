package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

/**
 * Section 18: Exactly three themes for NEO NOTES
 * 1. ◈ NEON CORE
 * 2. ♛ OBSIDIAN GOLD
 * 3. 🌸 SAKURA MODE
 * Demon and Luminary themes are completely eliminated.
 */
enum class NeoThemeId(val displayName: String, val badgeText: String) {
  NEON("Neon Core", "◈ NEON CORE"),
  GOLD("Obsidian Gold", "♛ OBSIDIAN GOLD"),
  SAKURA("Sakura Mode", "🌸 SAKURA MODE")
}

@Immutable
data class NeoThemePalette(
  val id: NeoThemeId,
  val bgDeep: Color,
  val bgNavy: Color,
  val accentCyan: Color,
  val accentViolet: Color,
  val accentMagenta: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val glassBg: Color,
  val glassBorder: Color,
  val glassGlow: Color,
  val isDark: Boolean = true,
  val fontFamily: FontFamily = FontFamily.Default
) {
  val primaryGradient: Brush
    get() = Brush.linearGradient(listOf(accentCyan, accentViolet, accentMagenta))

  val surfaceGradient: Brush
    get() = Brush.verticalGradient(listOf(bgNavy, bgDeep))

  val glassGradient: Brush
    get() = Brush.verticalGradient(listOf(glassBg, glassBg.copy(alpha = (glassBg.alpha * 0.7f).coerceIn(0f, 1f))))
}

val NeonPalette = NeoThemePalette(
  id = NeoThemeId.NEON,
  bgDeep = Color(0xFF080A18),
  bgNavy = Color(0xFF0E1025),
  accentCyan = Color(0xFF00F3FF),
  accentViolet = Color(0xFF8A2BE2),
  accentMagenta = Color(0xFFFF00CC),
  textPrimary = Color(0xFFFFFFFF),
  textSecondary = Color(0xA6FFFFFF),
  glassBg = Color(0x73141423),
  glassBorder = Color(0x1FFFFFFF),
  glassGlow = Color(0x1400F3FF),
  isDark = true,
  fontFamily = FontFamily.SansSerif
)

val ObsidianGoldPalette = NeoThemePalette(
  id = NeoThemeId.GOLD,
  bgDeep = Color(0xFF080804),
  bgNavy = Color(0xFF0E0D06),
  accentCyan = Color(0xFFD4AF37),
  accentViolet = Color(0xFFAA8833),
  accentMagenta = Color(0xFFC9A227),
  textPrimary = Color(0xFFFFFBF0),
  textSecondary = Color(0xA6E8DFC5),
  glassBg = Color(0x80141208),
  glassBorder = Color(0x1FD4AF37),
  glassGlow = Color(0x14D4AF37),
  isDark = true,
  fontFamily = FontFamily.SansSerif
)

val SakuraPalette = NeoThemePalette(
  id = NeoThemeId.SAKURA,
  bgDeep = Color(0xFF1A0812),
  bgNavy = Color(0xFF200E18),
  accentCyan = Color(0xFFFF69B4),
  accentViolet = Color(0xFFDB7093),
  accentMagenta = Color(0xFFFF1493),
  textPrimary = Color(0xFFFFF0F6),
  textSecondary = Color(0xA6F8D4E2),
  glassBg = Color(0x801E0C16),
  glassBorder = Color(0x1FFF69B4),
  glassGlow = Color(0x14FF69B4),
  isDark = true,
  fontFamily = FontFamily.SansSerif
)

fun getPaletteById(id: NeoThemeId): NeoThemePalette {
  return when (id) {
    NeoThemeId.NEON -> NeonPalette
    NeoThemeId.GOLD -> ObsidianGoldPalette
    NeoThemeId.SAKURA -> SakuraPalette
  }
}

val LocalNeoTheme = compositionLocalOf { NeonPalette }

fun NeoThemePalette.toM3ColorScheme(): ColorScheme {
  return darkColorScheme(
    primary = accentCyan,
    onPrimary = Color(0xFF080A18),
    primaryContainer = accentViolet.copy(alpha = 0.3f),
    onPrimaryContainer = textPrimary,
    secondary = accentViolet,
    onSecondary = Color.White,
    tertiary = accentMagenta,
    onTertiary = Color.White,
    background = bgDeep,
    onBackground = textPrimary,
    surface = bgNavy,
    onSurface = textPrimary,
    surfaceVariant = glassBg,
    onSurfaceVariant = textSecondary,
    outline = glassBorder
  )
}

@Composable
fun NeoNotesTheme(
  themeId: NeoThemeId = NeoThemeId.NEON,
  content: @Composable () -> Unit
) {
  val basePalette = getPaletteById(themeId)
  val m3ColorScheme = basePalette.toM3ColorScheme()

  CompositionLocalProvider(LocalNeoTheme provides basePalette) {
    MaterialTheme(
      colorScheme = m3ColorScheme,
      typography = Typography,
      content = content
    )
  }
}
