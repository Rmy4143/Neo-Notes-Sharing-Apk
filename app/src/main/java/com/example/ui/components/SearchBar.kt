package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalNeoTheme

@Composable
fun NeoSearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  placeholder: String = "Search notes, subjects, topics, tags..."
) {
  val theme = LocalNeoTheme.current
  val focusManager = LocalFocusManager.current
  var isFocused by remember { mutableStateOf(false) }

  val shape = RoundedCornerShape(16.dp)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .testTag("search_bar")
      .clip(shape)
      .background(theme.glassBg)
      .border(
        width = if (isFocused) 1.5.dp else 1.dp,
        color = if (isFocused) theme.accentCyan else theme.glassBorder,
        shape = shape
      )
      .padding(horizontal = 14.dp, vertical = 12.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Search",
        tint = if (isFocused) theme.accentCyan else theme.textSecondary,
        modifier = Modifier.size(20.dp)
      )

      Spacer(modifier = Modifier.width(10.dp))

      Box(modifier = Modifier.weight(1f)) {
        if (query.isEmpty()) {
          Text(
            text = placeholder,
            color = theme.textSecondary.copy(alpha = 0.6f),
            fontSize = 14.sp
          )
        }

        BasicTextField(
          value = query,
          onValueChange = onQueryChange,
          singleLine = true,
          textStyle = TextStyle(
            color = theme.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = theme.fontFamily
          ),
          cursorBrush = SolidColor(theme.accentCyan),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
          keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
          modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .testTag("search_input")
        )
      }

      if (query.isNotEmpty()) {
        IconButton(
          onClick = {
            onQueryChange("")
            focusManager.clearFocus()
          },
          modifier = Modifier
            .size(28.dp)
            .testTag("search_clear_button")
        ) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear search",
            tint = theme.textSecondary,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
