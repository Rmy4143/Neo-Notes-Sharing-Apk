package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalNeoTheme
import com.example.ui.theme.NeoThemeId

@Composable
fun ThemeBadge(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null
) {
  val theme = LocalNeoTheme.current
  val infiniteTransition = rememberInfiniteTransition(label = "theme_badge_anim")

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = when (theme.id) {
          NeoThemeId.NEON -> 1200
          NeoThemeId.GOLD -> 2400
          NeoThemeId.SAKURA -> 1800
        },
        easing = FastOutSlowInEasing
      ),
      repeatMode = RepeatMode.Reverse
    ),
    label = "badge_scale"
  )

  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.75f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = when (theme.id) {
          NeoThemeId.GOLD -> 2200
          else -> 1400
        },
        easing = LinearEasing
      ),
      repeatMode = RepeatMode.Reverse
    ),
    label = "badge_alpha"
  )

  val badgeColor = theme.accentCyan
  val badgeShape = RoundedCornerShape(12.dp)

  val baseMod = modifier
    .testTag("theme_badge")
    .scale(pulseScale)
    .alpha(pulseAlpha)
    .clip(badgeShape)
    .background(theme.glassBg)
    .border(
      width = 1.dp,
      color = badgeColor.copy(alpha = 0.35f),
      shape = badgeShape
    )
    .padding(horizontal = 10.dp, vertical = 5.dp)

  val finalMod = if (onClick != null) {
    baseMod.clickable { onClick() }
  } else {
    baseMod
  }

  Row(
    modifier = finalMod,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(badgeColor)
    )

    Text(
      text = "  ${theme.id.badgeText}",
      color = theme.textPrimary,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      fontFamily = theme.fontFamily,
      letterSpacing = 0.4.sp
    )
  }
}
