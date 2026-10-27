package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.ui.theme.LocalNeoTheme
import kotlin.math.sin

@Composable
fun BackgroundMesh(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  val theme = LocalNeoTheme.current

  val infiniteTransition = rememberInfiniteTransition(label = "mesh_anim")
  val animProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(12000, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "progress"
  )

  Box(modifier = modifier.fillMaxSize()) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val width = size.width
      val height = size.height

      // Base background gradient
      drawRect(
        brush = Brush.verticalGradient(
          colors = listOf(theme.bgNavy, theme.bgDeep)
        )
      )

      // Ambient glowing orbs
      val orb1X = width * 0.2f + sin(animProgress * Math.PI.toFloat() * 2f) * 40f
      val orb1Y = height * 0.15f + sin(animProgress * Math.PI.toFloat()) * 30f
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(theme.accentCyan.copy(alpha = if (theme.isDark) 0.16f else 0.08f), Color.Transparent),
          center = Offset(orb1X, orb1Y),
          radius = width * 0.65f
        ),
        radius = width * 0.65f,
        center = Offset(orb1X, orb1Y)
      )

      val orb2X = width * 0.85f - sin(animProgress * Math.PI.toFloat()) * 50f
      val orb2Y = height * 0.65f + sin(animProgress * Math.PI.toFloat() * 2f) * 40f
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(theme.accentViolet.copy(alpha = if (theme.isDark) 0.14f else 0.06f), Color.Transparent),
          center = Offset(orb2X, orb2Y),
          radius = width * 0.7f
        ),
        radius = width * 0.7f,
        center = Offset(orb2X, orb2Y)
      )

      val orb3X = width * 0.45f
      val orb3Y = height * 0.9f
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(theme.accentMagenta.copy(alpha = if (theme.isDark) 0.12f else 0.05f), Color.Transparent),
          center = Offset(orb3X, orb3Y),
          radius = width * 0.55f
        ),
        radius = width * 0.55f,
        center = Offset(orb3X, orb3Y)
      )

      // Subtle tech grid lines
      drawTechGrid(theme.glassBorder.copy(alpha = 0.04f))
    }

    content()
  }
}

private fun DrawScope.drawTechGrid(gridColor: Color) {
  val step = 48f
  val w = size.width
  val h = size.height

  var x = 0f
  while (x < w) {
    drawLine(
      color = gridColor,
      start = Offset(x, 0f),
      end = Offset(x, h),
      strokeWidth = 1f
    )
    x += step
  }

  var y = 0f
  while (y < h) {
    drawLine(
      color = gridColor,
      start = Offset(0f, y),
      end = Offset(w, y),
      strokeWidth = 1f
    )
    y += step
  }
}
