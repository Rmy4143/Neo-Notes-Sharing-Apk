package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalNeoTheme

@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(18.dp),
  borderGlow: Boolean = false,
  glowColor: Color? = null,
  onClick: (() -> Unit)? = null,
  testTag: String = "glass_card",
  contentPadding: Dp = 16.dp,
  content: @Composable BoxScope.() -> Unit
) {
  val theme = LocalNeoTheme.current
  val activeGlow = glowColor ?: theme.accentCyan

  val borderBrush = if (borderGlow) {
    Brush.linearGradient(
      colors = listOf(
        activeGlow.copy(alpha = 0.5f),
        theme.glassBorder,
        theme.accentViolet.copy(alpha = 0.3f)
      )
    )
  } else {
    Brush.linearGradient(
      colors = listOf(
        theme.glassBorder.copy(alpha = 0.35f),
        theme.glassBorder.copy(alpha = 0.12f)
      )
    )
  }

  val baseModifier = modifier
    .testTag(testTag)
    .clip(shape)
    .background(theme.glassBg)
    .border(BorderStroke(1.dp, borderBrush), shape)

  val finalModifier = if (onClick != null) {
    baseModifier.clickable { onClick() }
  } else {
    baseModifier
  }

  Box(
    modifier = finalModifier.padding(contentPadding),
    content = content
  )
}
