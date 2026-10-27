package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminStats
import com.example.data.model.Note
import com.example.data.model.User
import com.example.data.repository.AuthRepository
import com.example.data.repository.NotesRepository
import com.example.ui.components.DocumentViewerModal
import com.example.ui.components.GlassCard
import com.example.ui.components.NeoSearchBar
import com.example.ui.theme.LocalNeoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
  notesRepository: NotesRepository,
  authRepository: AuthRepository,
  coroutineScope: CoroutineScope,
  onShowToast: (String) -> Unit
) {
  val theme = LocalNeoTheme.current
  val currentUser by authRepository.currentUser.collectAsState()
  val adminStats by notesRepository.getAdminStats().collectAsState(
    initial = AdminStats()
  )
  val pendingNotes by notesRepository.allPendingNotes.collectAsState(initial = emptyList())
  val allNotes by notesRepository.allNotesRaw.collectAsState(initial = emptyList())
  val allUsers by authRepository.allUsers.collectAsState(initial = emptyList())

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf(
    "Pending (${pendingNotes.size})",
    "Repository (${allNotes.size})",
    "Storage Monitor",
    "Users (${allUsers.size})"
  )

  var previewNote by remember { mutableStateOf<Note?>(null) }
  var rejectingNote by remember { mutableStateOf<Note?>(null) }
  var rejectionReasonText by remember { mutableStateOf("") }
  var deletingNoteId by remember { mutableStateOf<String?>(null) }

  var repoSearchQuery by remember { mutableStateOf("") }

  val isAuthorized = currentUser?.role == "ADMIN" || currentUser?.role == "TEACHER"

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Admin Header
      item {
        Spacer(modifier = Modifier.height(8.dp))
        GlassCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          borderGlow = true,
          glowColor = theme.accentMagenta
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(theme.accentMagenta.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = theme.accentMagenta,
                modifier = Modifier.size(26.dp)
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "ACADEMIC REVIEW CENTER",
                color = theme.accentMagenta,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Text(
                text = "Faculty Moderation & Storage Oversight",
                color = theme.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = theme.fontFamily
              )
              Text(
                text = "Logged in as ${currentUser?.name ?: "Guest"} (${currentUser?.role ?: "NONE"})",
                color = theme.textSecondary,
                fontSize = 12.sp
              )
            }
          }
        }
      }

      // 2. Statistics Grid
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          AdminStatPill("Users", "${adminStats.totalUsers}", theme.accentCyan, Modifier.weight(1f))
          AdminStatPill("Pending", "${adminStats.pendingApprovals}", Color(0xFFFFB300), Modifier.weight(1f))
          AdminStatPill("Approved", "${adminStats.approvedNotes}", Color(0xFF00E676), Modifier.weight(1f))
          AdminStatPill("Storage", adminStats.totalStorageFormatted, theme.accentViolet, Modifier.weight(1f))
        }
      }

      // 3. Admin Tabs
      item {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color.Transparent,
          contentColor = theme.accentCyan,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = theme.accentCyan,
              height = 3.dp
            )
          },
          divider = {}
        ) {
          tabs.forEachIndexed { index, label ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  text = label,
                  color = if (selectedTab == index) theme.accentCyan else theme.textSecondary,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 12.sp
                )
              }
            )
          }
        }
      }

      // 4. Tab Content
      when (selectedTab) {
        0 -> {
          // PENDING APPROVALS
          if (pendingNotes.isEmpty()) {
            item {
              GlassCard(
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                contentPadding = 24.dp
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(36.dp)
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "Zero Pending Submissions",
                    color = theme.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "All uploaded notes have been reviewed by academic staff.",
                    color = theme.textSecondary,
                    fontSize = 12.sp
                  )
                }
              }
            }
          } else {
            items(pendingNotes, key = { it.id }) { note ->
              PendingNoteModerationCard(
                note = note,
                onPreview = { previewNote = note },
                onApprove = {
                  coroutineScope.launch {
                    notesRepository.approveNote(note.id, currentUser?.name ?: "Admin")
                    onShowToast("Approved '${note.title}'. Live on NitroFlare!")
                  }
                },
                onReject = {
                  rejectingNote = note
                  rejectionReasonText = ""
                }
              )
            }
          }
        }

        1 -> {
          // ALL REPOSITORY NOTES
          item {
            NeoSearchBar(
              query = repoSearchQuery,
              onQueryChange = { repoSearchQuery = it },
              placeholder = "Filter repository documents..."
            )
          }

          val filteredAllNotes = allNotes.filter {
            if (repoSearchQuery.isBlank()) true
            else it.title.contains(repoSearchQuery, ignoreCase = true) ||
                it.subject.contains(repoSearchQuery, ignoreCase = true) ||
                it.uploaderName.contains(repoSearchQuery, ignoreCase = true)
          }

          items(filteredAllNotes, key = { it.id }) { note ->
            RepositoryModerationItem(
              note = note,
              onPreview = { previewNote = note },
              onDelete = { deletingNoteId = note.id }
            )
          }
        }

        2 -> {
          // Section 13: NITROFLARE STORAGE & RETENTION MONITORING
          item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              // Monitoring Overview Card
              GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                borderGlow = true,
                glowColor = theme.accentCyan
              ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "NITROFLARE STORAGE MONITOR",
                      color = theme.accentCyan,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.sp
                    )
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF00E676).copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                      Text("PASSIVE AUDIT ACTIVE", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                  }

                  Text(
                    text = "Monitors document retention and inactivity on NitroFlare. Real usage metrics only — no artificial automated downloads.",
                    color = theme.textSecondary,
                    fontSize = 12.sp
                  )

                  Spacer(modifier = Modifier.height(6.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    StorageMetricBox("Total Files", "${allNotes.size}", Modifier.weight(1f))
                    StorageMetricBox("Metadata Size", adminStats.totalStorageFormatted, Modifier.weight(1f))
                    StorageMetricBox("Inactive (>60d)", "${adminStats.inactiveNotesCount}", Modifier.weight(1f))
                  }
                }
              }

              // Inactivity Warnings List
              val inactiveNotes = allNotes.filter { it.isInactiveOnNitroFlare }
              if (inactiveNotes.isNotEmpty()) {
                GlassCard(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  borderGlow = true,
                  glowColor = Color(0xFFFFB300)
                ) {
                  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "Inactivity Warning (${inactiveNotes.size} documents)",
                        color = Color(0xFFFFB300),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                    Text(
                      text = "The following notes have had no user access for over 60 days. NitroFlare may prune inactive files on unauthenticated tiers. Consider pinning or archiving.",
                      color = theme.textSecondary,
                      fontSize = 12.sp
                    )
                  }
                }

                Text(
                  text = "INACTIVE DOCUMENTS",
                  color = theme.accentCyan,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp,
                  modifier = Modifier.padding(top = 4.dp)
                )

                inactiveNotes.forEach { note ->
                  val lastAccess = note.lastAccessedAt?.let {
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
                  } ?: "Never accessed"

                  GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 10.dp) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(note.title, color = theme.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                        Text("${note.subject} • ${note.fileSize} • Last Access: $lastAccess", color = theme.textSecondary, fontSize = 11.sp)
                      }
                      OutlinedButton(
                        onClick = { previewNote = note },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                      ) {
                        Text("Inspect", fontSize = 11.sp, color = theme.accentCyan)
                      }
                    }
                  }
                }
              }

              // Recently Accessed Notes
              val recentlyAccessed = allNotes
                .filter { it.lastAccessedAt != null }
                .sortedByDescending { it.lastAccessedAt }
                .take(5)

              if (recentlyAccessed.isNotEmpty()) {
                Text(
                  text = "RECENTLY ACCESSED DOCUMENTS",
                  color = theme.accentCyan,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp,
                  modifier = Modifier.padding(top = 8.dp)
                )

                recentlyAccessed.forEach { note ->
                  val accessDate = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(note.lastAccessedAt!!))
                  GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 10.dp) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(note.title, color = theme.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                        Text("Downloads: ${note.downloadCount} • Accessed: $accessDate", color = theme.textSecondary, fontSize = 11.sp)
                      }
                      IconButton(onClick = { previewNote = note }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Visibility, contentDescription = "View", tint = theme.accentCyan, modifier = Modifier.size(16.dp))
                      }
                    }
                  }
                }
              }
            }
          }
        }

        3 -> {
          // REGISTERED USERS
          items(allUsers, key = { it.id }) { user ->
            UserModerationRow(user = user)
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(60.dp))
      }
    }

    // Rejection Reason Dialog
    rejectingNote?.let { note ->
      AlertDialog(
        onDismissRequest = { rejectingNote = null },
        title = {
          Text("Reject Submission", color = theme.textPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
          Column {
            Text(
              text = "Provide feedback for '${note.title}'. The author will receive a notification with this explanation.",
              color = theme.textSecondary,
              fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = rejectionReasonText,
              onValueChange = { rejectionReasonText = it },
              placeholder = { Text("e.g. Scans are blurry / Incomplete syllabus / Copyrighted text") },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = theme.accentMagenta,
                focusedTextColor = theme.textPrimary,
                unfocusedTextColor = theme.textPrimary
              ),
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              coroutineScope.launch {
                notesRepository.rejectNote(
                  note.id,
                  currentUser?.name ?: "Admin",
                  rejectionReasonText
                )
                rejectingNote = null
                onShowToast("Note rejected.")
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
          ) {
            Text("Confirm Rejection", fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          OutlinedButton(onClick = { rejectingNote = null }) {
            Text("Cancel", color = theme.textPrimary)
          }
        },
        containerColor = theme.bgNavy
      )
    }

    // Delete Confirmation Dialog (Section 16: Safe deletion)
    deletingNoteId?.let { noteId ->
      AlertDialog(
        onDismissRequest = { deletingNoteId = null },
        title = { Text("Delete Content", color = theme.textPrimary, fontWeight = FontWeight.Bold) },
        text = {
          Text(
            "Are you sure you want to remove this document's metadata from the campus database? NitroFlare cloud reference will be de-indexed.",
            color = theme.textSecondary,
            fontSize = 13.sp
          )
        },
        confirmButton = {
          Button(
            onClick = {
              coroutineScope.launch {
                notesRepository.deleteNote(noteId)
                deletingNoteId = null
                onShowToast("Document metadata removed.")
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
          ) {
            Text("Delete Permanently", fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          OutlinedButton(onClick = { deletingNoteId = null }) {
            Text("Cancel", color = theme.textPrimary)
          }
        },
        containerColor = theme.bgNavy
      )
    }

    // Document Preview
    previewNote?.let { note ->
      DocumentViewerModal(
        note = note,
        onDismiss = { previewNote = null },
        onDownload = {
          currentUser?.let { user ->
            coroutineScope.launch {
              notesRepository.recordDownload(note.id, user.id)
              notesRepository.storageManager.downloadToDevice(note.effectiveUrl, note.fileName)
              onShowToast("Downloaded '${note.fileName}'")
            }
          }
        }
      )
    }
  }
}

@Composable
private fun StorageMetricBox(title: String, value: String, modifier: Modifier = Modifier) {
  val theme = LocalNeoTheme.current
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(theme.bgDeep)
      .border(1.dp, theme.glassBorder, RoundedCornerShape(8.dp))
      .padding(8.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = value, color = theme.accentCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
      Text(text = title, color = theme.textSecondary, fontSize = 10.sp)
    }
  }
}

@Composable
private fun AdminStatPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
  val theme = LocalNeoTheme.current
  GlassCard(
    modifier = modifier,
    contentPadding = 8.dp,
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = value,
        color = color,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = theme.fontFamily
      )
      Text(
        text = label,
        color = theme.textSecondary,
        fontSize = 11.sp
      )
    }
  }
}

@Composable
private fun PendingNoteModerationCard(
  note: Note,
  onPreview: () -> Unit,
  onApprove: () -> Unit,
  onReject: () -> Unit
) {
  val theme = LocalNeoTheme.current
  val uploadDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(note.createdAt))

  GlassCard(
    modifier = Modifier.fillMaxWidth(),
    borderGlow = true,
    glowColor = Color(0xFFFFB300),
    testTag = "pending_card_${note.id}"
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x33FFB300))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "PENDING REVIEW",
            color = Color(0xFFFFB300),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Section 10 requirements: File type, File size, Upload date
        Text(
          text = "${note.fileType} • ${note.fileSize} • $uploadDate",
          color = theme.textSecondary,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Section 10: Title, Uploader, Subject, Semester
      Text(
        text = note.title,
        color = theme.textPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = theme.fontFamily
      )

      Text(
        text = "By ${note.uploaderName} • Subject: ${note.subject} • ${note.semester} (${note.branch})",
        color = theme.textSecondary,
        fontSize = 12.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = note.description,
        color = theme.textSecondary,
        fontSize = 12.sp,
        maxLines = 2
      )

      Spacer(modifier = Modifier.height(8.dp))

      // NitroFlare URL info
      Text(
        text = "NitroFlare: ${note.effectiveUrl}",
        color = theme.accentCyan.copy(alpha = 0.8f),
        fontSize = 10.sp,
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onPreview,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp), tint = theme.accentCyan)
          Spacer(modifier = Modifier.width(4.dp))
          Text("Inspect", color = theme.accentCyan, fontSize = 12.sp)
        }

        Button(
          onClick = onApprove,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
          modifier = Modifier.weight(1f).testTag("approve_btn_${note.id}")
        ) {
          Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
          Spacer(modifier = Modifier.width(4.dp))
          Text("Approve", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onReject,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
          modifier = Modifier.weight(1f).testTag("reject_btn_${note.id}")
        ) {
          Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Reject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun RepositoryModerationItem(
  note: Note,
  onPreview: () -> Unit,
  onDelete: () -> Unit
) {
  val theme = LocalNeoTheme.current
  val statusColor = when (note.status) {
    "APPROVED" -> Color(0xFF00E676)
    "REJECTED" -> Color(0xFFFF5252)
    else -> Color(0xFFFFB300)
  }

  GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 12.dp) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(statusColor)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = note.title,
            color = theme.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
          )
        }
        Text(
          text = "${note.subject} • ${note.uploaderName} • ${note.downloadCount} dl • ${note.fileSize}",
          color = theme.textSecondary,
          fontSize = 11.sp
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        IconButton(onClick = onPreview, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Default.Visibility, contentDescription = "View", tint = theme.accentCyan, modifier = Modifier.size(16.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("delete_note_${note.id}")) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}

@Composable
private fun UserModerationRow(user: User) {
  val theme = LocalNeoTheme.current
  GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 12.dp) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(theme.accentCyan.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = user.name.take(2).uppercase(),
          color = theme.accentCyan,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = user.name,
            color = theme.textPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (user.role == "ADMIN") theme.accentMagenta.copy(alpha = 0.2f) else theme.accentCyan.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = user.role,
              color = if (user.role == "ADMIN") theme.accentMagenta else theme.accentCyan,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
        Text(
          text = "${user.email} • ${user.branch}",
          color = theme.textSecondary,
          fontSize = 11.sp
        )
      }
    }
  }
}
