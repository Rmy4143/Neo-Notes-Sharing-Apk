package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.NotificationItem
import com.example.data.repository.NotificationRepository
import com.example.ui.components.GlassCard
import com.example.ui.theme.LocalNeoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsDialog(
  userId: String,
  notificationRepository: NotificationRepository,
  coroutineScope: CoroutineScope,
  onDismiss: () -> Unit
) {
  val theme = LocalNeoTheme.current
  val notifications by notificationRepository.getNotifications(userId).collectAsState(initial = emptyList())

  Dialog(onDismissRequest = onDismiss) {
    GlassCard(
      modifier = Modifier
        .fillMaxWidth()
        .height(540.dp),
      shape = RoundedCornerShape(24.dp),
      borderGlow = true,
      glowColor = theme.accentCyan,
      testTag = "notifications_dialog"
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = theme.accentCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "NOTIFICATIONS",
              color = theme.textPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = theme.fontFamily
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            if (notifications.any { !it.isRead }) {
              IconButton(
                onClick = {
                  coroutineScope.launch { notificationRepository.markAllAsRead(userId) }
                },
                modifier = Modifier.testTag("mark_all_read_btn")
              ) {
                Icon(Icons.Default.DoneAll, contentDescription = "Mark all read", tint = theme.accentCyan, modifier = Modifier.size(20.dp))
              }
            }

            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = theme.textSecondary, modifier = Modifier.size(20.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (notifications.isEmpty()) {
          Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Text("No notifications yet", color = theme.textSecondary, fontSize = 13.sp)
          }
        } else {
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(notifications, key = { it.id }) { item ->
              NotificationRow(
                item = item,
                onClick = {
                  if (!item.isRead) {
                    coroutineScope.launch { notificationRepository.markAsRead(item.id) }
                  }
                }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun NotificationRow(item: NotificationItem, onClick: () -> Unit) {
  val theme = LocalNeoTheme.current

  val icon = when (item.type) {
    "APPROVAL" -> Icons.Default.CheckCircle
    "REJECTION" -> Icons.Default.Error
    "MILESTONE" -> Icons.Default.Star
    else -> Icons.Default.AutoAwesome
  }

  val iconColor = when (item.type) {
    "APPROVAL" -> Color(0xFF00E676)
    "REJECTION" -> Color(0xFFFF5252)
    "MILESTONE" -> Color(0xFFFFB300)
    else -> theme.accentCyan
  }

  val formattedTime = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    .format(Date(item.createdAt))

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(if (!item.isRead) theme.accentCyan.copy(alpha = 0.12f) else theme.glassBg)
      .clickable { onClick() }
      .padding(12.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(iconColor.copy(alpha = 0.2f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = item.title,
          color = theme.textPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
        if (!item.isRead) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(theme.accentCyan)
          )
        }
      }

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = item.message,
        color = theme.textSecondary,
        fontSize = 12.sp,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = formattedTime,
        color = theme.textSecondary.copy(alpha = 0.6f),
        fontSize = 10.sp
      )
    }
  }
}
