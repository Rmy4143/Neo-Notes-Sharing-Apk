package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import com.example.ui.theme.LocalNeoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteCard(
  note: Note,
  isBookmarked: Boolean,
  onView: () -> Unit,
  onDownload: () -> Unit,
  onToggleBookmark: () -> Unit,
  modifier: Modifier = Modifier,
  showStatus: Boolean = false
) {
  val theme = LocalNeoTheme.current

  val fileTypeColor = when (note.fileType.uppercase()) {
    "PDF" -> Color(0xFFFF4B4B)
    "DOC", "DOCX" -> Color(0xFF2B7CD3)
    "PPT", "PPTX" -> Color(0xFFFF7A00)
    else -> theme.accentCyan
  }

  val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    .format(Date(note.createdAt))

  GlassCard(
    modifier = modifier.fillMaxWidth(),
    borderGlow = isBookmarked,
    glowColor = theme.accentCyan,
    testTag = "note_card_${note.id}"
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Header: File Type Badge + Subject + Bookmark Icon
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // File type badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(fileTypeColor.copy(alpha = 0.2f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = note.fileType.uppercase(),
              color = fileTypeColor,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Subject Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(theme.accentViolet.copy(alpha = 0.15f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = note.subject,
              color = theme.accentCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // Bookmark Toggle
        IconButton(
          onClick = onToggleBookmark,
          modifier = Modifier
            .size(36.dp)
            .testTag("bookmark_button_${note.id}")
        ) {
          Icon(
            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = "Bookmark",
            tint = if (isBookmarked) theme.accentCyan else theme.textSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Title
      Text(
        text = note.title,
        color = theme.textPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        fontFamily = theme.fontFamily
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Description
      Text(
        text = note.description,
        color = theme.textSecondary,
        fontSize = 13.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Tags
      if (note.tags.isNotBlank()) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.padding(bottom = 8.dp)
        ) {
          note.tags.split(",").take(3).forEach { rawTag ->
            val tag = rawTag.trim()
            if (tag.isNotEmpty()) {
              Text(
                text = "#$tag",
                color = theme.accentMagenta.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      // Metadata: Branch, Semester, Uploader
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            tint = theme.textSecondary,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "${note.branch} • ${note.semester}",
            color = theme.textSecondary,
            fontSize = 11.sp
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = theme.textSecondary,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = note.uploaderName,
            color = theme.textSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Status pill (if shown)
      if (showStatus) {
        Spacer(modifier = Modifier.height(8.dp))
        val statusColor = when (note.status) {
          "APPROVED" -> Color(0xFF00E676)
          "REJECTED" -> Color(0xFFFF5252)
          else -> Color(0xFFFFB300)
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(statusColor)
          )
          Text(
            text = "Status: ${note.status}",
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
          if (note.rejectionReason != null && note.status == "REJECTED") {
            Text(
              text = "(${note.rejectionReason})",
              color = theme.textSecondary,
              fontSize = 11.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Bottom bar: File Size, Downloads count, Actions (View, Download)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = note.fileSize,
            color = theme.textSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "•",
            color = theme.textSecondary,
            fontSize = 12.sp
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = null,
              tint = theme.textSecondary,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "${note.downloadCount}",
              color = theme.textSecondary,
              fontSize = 12.sp
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // Preview button
          OutlinedButton(
            onClick = onView,
            shape = RoundedCornerShape(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("preview_note_${note.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Visibility,
              contentDescription = "Preview",
              modifier = Modifier.size(15.dp),
              tint = theme.accentCyan
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "View",
              color = theme.accentCyan,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          // Download button
          Button(
            onClick = onDownload,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = theme.accentCyan,
              contentColor = if (theme.isDark) Color(0xFF080A18) else Color.White
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("download_note_${note.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = "Download",
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Get",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
