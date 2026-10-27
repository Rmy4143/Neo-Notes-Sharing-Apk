package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.NeoThemeId
import com.example.ui.theme.getPaletteById
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.max

data class ThemeTransitionRequest(
  val targetTheme: NeoThemeId,
  val origin: Offset = Offset.Zero
)

@Composable
fun WaterWaveTransitionOverlay(
  activeRequest: ThemeTransitionRequest?,
  reducedMotion: Boolean = false,
  onApplyTheme: (NeoThemeId) -> Unit,
  onTransitionFinished: () -> Unit
) {
  if (activeRequest == null) return

  val animatable = remember { Animatable(0f) }
  val targetPalette = remember(activeRequest.targetTheme) {
    getPaletteById(activeRequest.targetTheme)
  }

  LaunchedEffect(activeRequest) {
    if (reducedMotion) {
      onApplyTheme(activeRequest.targetTheme)
      onTransitionFinished()
      return@LaunchedEffect
    }

    animatable.snapTo(0f)
    var themeSwitched = false

    // Animate from 0 to 1 with cubic-bezier easing over 1100ms
    animatable.animateTo(
      targetValue = 1f,
      animationSpec = tween(
        durationMillis = 1100,
        easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
      )
    ) {
      // Step 5: At ~50%-55% progress, apply the new theme!
      if (!themeSwitched && this.value >= 0.52f) {
        themeSwitched = true
        onApplyTheme(activeRequest.targetTheme)
      }
    }

    onTransitionFinished()
  }

  val progress = animatable.value

  Canvas(modifier = Modifier.fillMaxSize()) {
    val origin = if (activeRequest.origin == Offset.Zero) {
      Offset(size.width * 0.85f, size.height * 0.08f)
    } else {
      activeRequest.origin
    }

    val maxRadius = max(
      hypot(origin.x.toDouble(), origin.y.toDouble()),
      max(
        hypot((size.width - origin.x).toDouble(), origin.y.toDouble()),
        max(
          hypot(origin.x.toDouble(), (size.height - origin.y).toDouble()),
          hypot((size.width - origin.x).toDouble(), (size.height - origin.y).toDouble())
        )
      )
    ).toFloat() * 1.25f

    drawWaterWaveEffect(
      origin = origin,
      maxRadius = maxRadius,
      progress = progress,
      accentColor = targetPalette.accentCyan,
      secondaryColor = targetPalette.accentViolet,
      themeBg = targetPalette.bgNavy
    )
  }
}

private fun DrawScope.drawWaterWaveEffect(
  origin: Offset,
  maxRadius: Float,
  progress: Float,
  accentColor: Color,
  secondaryColor: Color,
  themeBg: Color
) {
  // Current expanding front radius
  val currentRadius = maxRadius * progress

  // 1. Expanding wavefront body fill
  val fillAlpha = when {
    progress < 0.5f -> (progress * 1.6f).coerceIn(0f, 0.75f)
    progress < 0.85f -> 0.75f
    else -> ((1f - progress) / 0.15f * 0.75f).coerceIn(0f, 0.75f)
  }

  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        themeBg.copy(alpha = fillAlpha),
        accentColor.copy(alpha = (fillAlpha * 0.7f).coerceIn(0f, 1f)),
        secondaryColor.copy(alpha = (fillAlpha * 0.4f).coerceIn(0f, 1f))
      ),
      center = origin,
      radius = (currentRadius).coerceAtLeast(10f)
    ),
    center = origin,
    radius = currentRadius
  )

  // 2. Primary Wavefront Glowing Ring
  val ringAlpha = when {
    progress < 0.2f -> (progress / 0.2f).coerceIn(0f, 1f)
    progress < 0.8f -> 0.95f
    else -> ((1f - progress) / 0.2f).coerceIn(0f, 1f)
  }

  drawCircle(
    brush = Brush.sweepGradient(
      colors = listOf(
        accentColor.copy(alpha = ringAlpha),
        secondaryColor.copy(alpha = ringAlpha * 0.8f),
        accentColor.copy(alpha = ringAlpha)
      ),
      center = origin
    ),
    center = origin,
    radius = currentRadius,
    style = Stroke(width = (14f * (1f - progress * 0.5f)).coerceAtLeast(2f))
  )

  // 3. Secondary concentric ripple rings (Echo Waves)
  val echo1Radius = currentRadius * 0.82f
  if (echo1Radius > 0f) {
    drawCircle(
      color = accentColor.copy(alpha = (ringAlpha * 0.45f).coerceIn(0f, 1f)),
      center = origin,
      radius = echo1Radius,
      style = Stroke(width = 4f)
    )
  }

  val echo2Radius = currentRadius * 0.65f
  if (echo2Radius > 0f) {
    drawCircle(
      color = secondaryColor.copy(alpha = (ringAlpha * 0.25f).coerceIn(0f, 1f)),
      center = origin,
      radius = echo2Radius,
      style = Stroke(width = 2.5f)
    )
  }
}
