package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityItem
import com.example.data.model.Note
import com.example.data.repository.AuthRepository
import com.example.data.repository.NotesRepository
import com.example.ui.components.DocumentViewerModal
import com.example.ui.components.GlassCard
import com.example.ui.components.NoteCard
import com.example.ui.theme.LocalNeoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
  notesRepository: NotesRepository,
  authRepository: AuthRepository,
  coroutineScope: CoroutineScope,
  onNavigateToUpload: () -> Unit,
  onShowToast: (String) -> Unit
) {
  val theme = LocalNeoTheme.current
  val currentUser by authRepository.currentUser.collectAsState()

  val user = currentUser
  if (user == null) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text("Please log in to view your dashboard", color = theme.textSecondary)
    }
    return
  }

  val userNotes by notesRepository.getNotesForUser(user.id).collectAsState(initial = emptyList())
  val bookmarkedNotes by notesRepository.getBookmarkedNotes(user.id).collectAsState(initial = emptyList())
  val stats by notesRepository.getDashboardStats(user.id).collectAsState(
    initial = com.example.data.model.DashboardStats()
  )
  val activities by notesRepository.getRecentActivities(user.id).collectAsState(initial = emptyList())

  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val tabs = listOf("My Uploads", "Bookmarks", "Activity")

  var previewNote by remember { mutableStateOf<Note?>(null) }

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Profile Welcome Banner
      item {
        Spacer(modifier = Modifier.height(8.dp))
        GlassCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          borderGlow = true
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(theme.accentCyan.copy(alpha = 0.2f))
                .border(1.5.dp, theme.accentCyan, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = user.name.take(2).uppercase(),
                color = theme.accentCyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = user.name,
                  color = theme.textPrimary,
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = theme.fontFamily
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                      if (user.role == "ADMIN") theme.accentMagenta.copy(alpha = 0.2f)
                      else theme.accentCyan.copy(alpha = 0.15f)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = user.role,
                    color = if (user.role == "ADMIN") theme.accentMagenta else theme.accentCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Spacer(modifier = Modifier.height(2.dp))

              Text(
                text = "${user.branch} • ${user.semester}",
                color = theme.textSecondary,
                fontSize = 12.sp
              )
              Text(
                text = user.college,
                color = theme.textSecondary.copy(alpha = 0.7f),
                fontSize = 11.sp
              )
            }
          }
        }
      }

      // 2. Metrics / Stat Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatCard(
            title = "Uploaded",
            count = "${stats.totalUploaded}",
            icon = Icons.Default.CloudUpload,
            color = theme.accentCyan,
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "Approved",
            count = "${stats.totalApproved}",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF00E676),
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "Pending",
            count = "${stats.totalPending}",
            icon = Icons.Default.HourglassTop,
            color = Color(0xFFFFB300),
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "Downloads",
            count = "${stats.totalDownloads}",
            icon = Icons.Default.Download,
            color = theme.accentViolet,
            modifier = Modifier.weight(1f)
          )
        }
      }

      // 3. Tab Selector
      item {
        TabRow(
          selectedTabIndex = selectedTabIndex,
          containerColor = Color.Transparent,
          contentColor = theme.accentCyan,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
              color = theme.accentCyan,
              height = 3.dp
            )
          },
          divider = {}
        ) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTabIndex == index,
              onClick = { selectedTabIndex = index },
              text = {
                Text(
                  text = title,
                  color = if (selectedTabIndex == index) theme.accentCyan else theme.textSecondary,
                  fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 13.sp
                )
              }
            )
          }
        }
      }

      // 4. Tab Content
      when (selectedTabIndex) {
        0 -> {
          // My Uploads
          if (userNotes.isEmpty()) {
            item {
              EmptyDashboardState(
                message = "You haven't uploaded any notes yet.",
                ctaText = "Upload Your First Note",
                onCtaClick = onNavigateToUpload
              )
            }
          } else {
            items(userNotes, key = { it.id }) { note ->
              NoteCard(
                note = note,
                isBookmarked = false,
                onView = { previewNote = note },
                onDownload = {
                  notesRepository.storageManager.downloadToDevice(note.effectiveUrl, note.fileName)
                  coroutineScope.launch {
                    notesRepository.recordDownload(note.id, user.id)
                  }
                },
                onToggleBookmark = {},
                showStatus = true
              )
            }
          }
        }
        1 -> {
          // Bookmarks
          if (bookmarkedNotes.isEmpty()) {
            item {
              EmptyDashboardState(
                message = "No bookmarked notes. Bookmark notes to access them quickly here.",
                ctaText = null,
                onCtaClick = {}
              )
            }
          } else {
            items(bookmarkedNotes, key = { it.id }) { note ->
              NoteCard(
                note = note,
                isBookmarked = true,
                onView = { previewNote = note },
                onDownload = {
                  notesRepository.storageManager.downloadToDevice(note.effectiveUrl, note.fileName)
                  coroutineScope.launch {
                    notesRepository.recordDownload(note.id, user.id)
                  }
                },
                onToggleBookmark = {
                  coroutineScope.launch {
                    notesRepository.toggleBookmark(note.id, user.id)
                    onShowToast("Removed from bookmarks")
                  }
                }
              )
            }
          }
        }
        2 -> {
          // Activity Timeline
          if (activities.isEmpty()) {
            item {
              EmptyDashboardState(
                message = "No recent activity recorded yet.",
                ctaText = null,
                onCtaClick = {}
              )
            }
          } else {
            items(activities, key = { it.id }) { activity ->
              ActivityRow(activity = activity)
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(60.dp))
      }
    }

    // Preview Modal
    previewNote?.let { note ->
      DocumentViewerModal(
        note = note,
        onDismiss = { previewNote = null },
        onDownload = {
          notesRepository.storageManager.downloadToDevice(note.effectiveUrl, note.fileName)
          coroutineScope.launch {
            notesRepository.recordDownload(note.id, user.id)
          }
        }
      )
    }
  }
}

@Composable
private fun StatCard(
  title: String,
  count: String,
  icon: ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  val theme = LocalNeoTheme.current
  GlassCard(
    modifier = modifier,
    contentPadding = 10.dp,
    shape = RoundedCornerShape(14.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = count,
        color = theme.textPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = theme.fontFamily
      )
      Text(
        text = title,
        color = theme.textSecondary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
private fun ActivityRow(activity: ActivityItem) {
  val theme = LocalNeoTheme.current
  val formattedTime = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    .format(Date(activity.timestamp))

  GlassCard(
    modifier = Modifier.fillMaxWidth(),
    contentPadding = 12.dp,
    shape = RoundedCornerShape(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(theme.accentCyan.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Timeline,
          contentDescription = null,
          tint = theme.accentCyan,
          modifier = Modifier.size(16.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = activity.title,
          color = theme.textPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium
        )
        Text(
          text = formattedTime,
          color = theme.textSecondary,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
private fun EmptyDashboardState(
  message: String,
  ctaText: String?,
  onCtaClick: () -> Unit
) {
  val theme = LocalNeoTheme.current
  GlassCard(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 24.dp),
    contentPadding = 24.dp
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = message,
        color = theme.textSecondary,
        fontSize = 13.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
      if (ctaText != null) {
        Spacer(modifier = Modifier.height(14.dp))
        Button(
          onClick = onCtaClick,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = theme.accentCyan)
        ) {
          Text(ctaText, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
