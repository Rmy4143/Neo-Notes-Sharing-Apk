package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalNeoTheme

enum class SortOption(val label: String) {
  NEWEST("Newest"),
  MOST_DOWNLOADED("Most Downloaded"),
  MOST_BOOKMARKED("Most Bookmarked"),
  ALPHABETICAL("A-Z")
}

@Composable
fun FilterChipRow(
  options: List<String>,
  selectedOption: String,
  onOptionSelected: (String) -> Unit,
  modifier: Modifier = Modifier,
  label: String? = null
) {
  val theme = LocalNeoTheme.current

  Column(modifier = modifier.fillMaxWidth()) {
    if (label != null) {
      Text(
        text = label,
        color = theme.textSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      options.forEach { option ->
        val isSelected = option == selectedOption

        val bgColor by animateColorAsState(
          targetValue = if (isSelected) theme.accentCyan.copy(alpha = 0.22f) else theme.glassBg,
          label = "chip_bg"
        )
        val borderColor by animateColorAsState(
          targetValue = if (isSelected) theme.accentCyan else theme.glassBorder,
          label = "chip_border"
        )
        val textColor by animateColorAsState(
          targetValue = if (isSelected) theme.accentCyan else theme.textSecondary,
          label = "chip_text"
        )

        val shape = RoundedCornerShape(20.dp)

        Box(
          modifier = Modifier
            .testTag("filter_chip_${option.lowercase().replace(" ", "_")}")
            .clip(shape)
            .background(bgColor)
            .border(1.dp, borderColor, shape)
            .clickable { onOptionSelected(option) }
            .padding(horizontal = 14.dp, vertical = 7.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = option,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
          )
        }
      }
    }
  }
}
