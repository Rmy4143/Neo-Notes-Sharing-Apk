package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LocalNeoTheme
import com.example.ui.theme.NeoThemeId
import com.example.ui.theme.getPaletteById

@Composable
fun ThemeSelectorDialog(
  currentThemeId: NeoThemeId,
  onThemeSelected: (NeoThemeId, Offset) -> Unit,
  onDismiss: () -> Unit
) {
  val theme = LocalNeoTheme.current

  Dialog(onDismissRequest = onDismiss) {
    GlassCard(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      shape = RoundedCornerShape(24.dp),
      borderGlow = true,
      glowColor = theme.accentCyan,
      testTag = "theme_selector_dialog"
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "SELECT THEME IDENTITY",
              color = theme.accentCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp
            )
            Text(
              text = "Cinematic Theme Engine",
              color = theme.textPrimary,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = theme.fontFamily
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_theme_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = theme.textSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Themes List
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          NeoThemeId.values().forEach { themeId ->
            val palette = getPaletteById(themeId)
            val isSelected = themeId == currentThemeId
            var itemCenterOffset = Offset.Zero

            val itemShape = RoundedCornerShape(14.dp)
            val borderColor = if (isSelected) palette.accentCyan else theme.glassBorder

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("theme_option_${themeId.name.lowercase()}")
                .onGloballyPositioned { coordinates ->
                  val pos = coordinates.positionInWindow()
                  itemCenterOffset = Offset(
                    pos.x + coordinates.size.width / 2f,
                    pos.y + coordinates.size.height / 2f
                  )
                }
                .clip(itemShape)
                .background(if (isSelected) palette.accentCyan.copy(alpha = 0.15f) else theme.glassBg)
                .border(1.dp, borderColor, itemShape)
                .clickable {
                  onThemeSelected(themeId, itemCenterOffset)
                  onDismiss()
                }
                .padding(horizontal = 14.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                // Color previews capsule
                Row(
                  modifier = Modifier
                    .clip(CircleShape)
                    .background(palette.bgDeep)
                    .padding(4.dp),
                  horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(10.dp)
                      .clip(CircleShape)
                      .background(palette.accentCyan)
                  )
                  Box(
                    modifier = Modifier
                      .size(10.dp)
                      .clip(CircleShape)
                      .background(palette.accentViolet)
                  )
                  Box(
                    modifier = Modifier
                      .size(10.dp)
                      .clip(CircleShape)
                      .background(palette.accentMagenta)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = themeId.displayName,
                    color = palette.accentCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = palette.fontFamily
                  )
                  Text(
                    text = themeId.badgeText,
                    color = palette.textSecondary,
                    fontSize = 11.sp
                  )
                }
              }

              if (isSelected) {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(palette.accentCyan),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = palette.bgDeep,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
