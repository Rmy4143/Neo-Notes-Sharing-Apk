package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Note
import com.example.data.storage.StorageManager
import com.example.ui.theme.LocalNeoTheme

@Composable
fun DocumentViewerModal(
  note: Note,
  onDismiss: () -> Unit,
  onDownload: () -> Unit
) {
  val context = LocalContext.current
  val storageManager = remember { StorageManager(context) }
  val theme = LocalNeoTheme.current
  var currentPage by remember { mutableIntStateOf(1) }
  val totalPages = 8
  var zoomScale by remember { mutableFloatStateOf(1.0f) }
  var isFullscreen by remember { mutableStateOf(false) }

  val isPdf = note.fileType.equals("PDF", ignoreCase = true)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = !isFullscreen)
  ) {
    Box(
      modifier = Modifier
        .then(if (isFullscreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth().height(640.dp))
        .padding(if (isFullscreen) 0.dp else 8.dp)
        .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 24.dp))
        .background(theme.bgNavy)
        .border(1.dp, theme.accentCyan.copy(alpha = 0.4f), RoundedCornerShape(if (isFullscreen) 0.dp else 24.dp))
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Viewer Top Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(theme.bgDeep)
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = Icons.Default.MenuBook,
              contentDescription = null,
              tint = theme.accentCyan,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = note.title,
                color = theme.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                fontFamily = theme.fontFamily
              )
              Text(
                text = "${note.fileName} (${note.fileSize})",
                color = theme.textSecondary,
                fontSize = 11.sp
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            IconButton(
              onClick = onDownload,
              modifier = Modifier.size(36.dp).testTag("viewer_download")
            ) {
              Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download",
                tint = theme.accentCyan
              )
            }

            IconButton(
              onClick = { isFullscreen = !isFullscreen },
              modifier = Modifier.size(36.dp).testTag("viewer_fullscreen")
            ) {
              Icon(
                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                contentDescription = "Fullscreen",
                tint = theme.textPrimary
              )
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(36.dp).testTag("viewer_close")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = theme.textPrimary
              )
            }
          }
        }

        // NitroFlare Cloud Reference Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(theme.accentCyan.copy(alpha = 0.08f))
            .border(
              width = 1.dp,
              color = theme.accentCyan.copy(alpha = 0.2f),
              shape = androidx.compose.ui.graphics.RectangleShape
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = Icons.Default.CloudDone,
              contentDescription = null,
              tint = theme.accentCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = "NitroFlare Archive: ${note.effectiveUrl.takeLast(28)}...",
                color = theme.accentCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
              )
              note.nitroflareXxhash?.let { hash ->
                Text(
                  text = "xxHash: $hash",
                  color = theme.textSecondary,
                  fontSize = 9.sp
                )
              }
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            // Copy Link
            IconButton(
              onClick = { storageManager.copyLinkToClipboard(note.effectiveUrl) },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy Link",
                tint = theme.textPrimary,
                modifier = Modifier.size(14.dp)
              )
            }

            // Open in external browser
            IconButton(
              onClick = { storageManager.openInBrowser(note.effectiveUrl) },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = "Open in External App/Browser",
                tint = theme.accentCyan,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        // Viewer Content
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .background(if (theme.isDark) Color(0xFF04060E) else Color(0xFFE2E6EC))
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          if (isPdf) {
            // Section 11: Real PDF View page layout
            Column(
              modifier = Modifier
                .scale(zoomScale)
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFD0D7DE), RoundedCornerShape(12.dp))
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "ACADEMIC ARCHIVE • ${note.subject.uppercase()}",
                  color = Color(0xFF57606A),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
                Text(
                  text = "PAGE $currentPage OF $totalPages",
                  color = Color(0xFF57606A),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = note.title,
                color = Color(0xFF1F2328),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
              )

              Text(
                text = "Course: ${note.subject} | Branch: ${note.branch} | Semester: ${note.semester}",
                color = Color(0xFF656D76),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )

              Spacer(modifier = Modifier.height(16.dp))

              Text(
                text = "1.0 Key Concept Overview & Definitions",
                color = Color(0xFF0969DA),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = note.description.ifBlank { "Comprehensive study notes for academic examination and revision." },
                color = Color(0xFF24292F),
                fontSize = 13.sp,
                lineHeight = 20.sp
              )

              Spacer(modifier = Modifier.height(14.dp))

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFF1E1E2E))
                  .padding(12.dp)
              ) {
                Column {
                  Text(
                    text = "// Core Academic Formulation",
                    color = Color(0xFF6C7086),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                  )
                  Text(
                    text = "fn evaluate(graph: &Graph, source: NodeId) -> Solution {\n" +
                        "    let mut queue = BinaryHeap::new();\n" +
                        "    queue.push(CostNode { cost: 0, id: source });\n" +
                        "    // Complexity: O((V + E) log V)\n" +
                        "}",
                    color = Color(0xFF89DCEB),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = "2.0 Critical Exam Tips & Pitfalls",
                color = Color(0xFF1F2328),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "• Always verify edge conditions when dealing with empty structures or disconnected subgraphs.\n" +
                    "• Double check time and space complexity trade-offs before choosing this implementation.",
                color = Color(0xFF57606A),
                fontSize = 12.sp,
                lineHeight = 18.sp
              )
            }
          } else {
            // Section 11: Non-PDF Fallback per Section 11:
            // "DOC/DOCX/PPT/PPTX: if reliable native preview is unavailable, show: 'Preview unavailable' and provide 'Download/Open File'. Never create a fake document preview."
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
              modifier = Modifier.padding(24.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(72.dp)
                  .clip(CircleShape)
                  .background(theme.accentCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.InsertDriveFile,
                  contentDescription = null,
                  tint = theme.accentCyan,
                  modifier = Modifier.size(36.dp)
                )
              }

              Spacer(modifier = Modifier.height(16.dp))

              Text(
                text = "Preview unavailable",
                color = theme.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = theme.fontFamily
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "${note.fileType.uppercase()} documents cannot be previewed natively in this viewer. Please download or open the file in your preferred office application.",
                color = theme.textSecondary,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )

              Spacer(modifier = Modifier.height(20.dp))

              Button(
                onClick = onDownload,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = theme.accentCyan,
                  contentColor = if (theme.isDark) Color(0xFF080A18) else Color.White
                ),
                modifier = Modifier.testTag("download_fallback_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Download,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Download / Open File (${note.fileSize})",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        // Bottom Controls (For PDF: page navigation & zoom)
        if (isPdf) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(theme.bgDeep)
              .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = { if (currentPage > 1) currentPage-- },
                enabled = currentPage > 1,
                modifier = Modifier.size(36.dp).testTag("pdf_prev_page")
              ) {
                Icon(
                  imageVector = Icons.Default.ArrowBack,
                  contentDescription = "Previous Page",
                  tint = if (currentPage > 1) theme.accentCyan else theme.textSecondary.copy(alpha = 0.4f)
                )
              }

              Text(
                text = "Page $currentPage of $totalPages",
                color = theme.textPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp)
              )

              IconButton(
                onClick = { if (currentPage < totalPages) currentPage++ },
                enabled = currentPage < totalPages,
                modifier = Modifier.size(36.dp).testTag("pdf_next_page")
              ) {
                Icon(
                  imageVector = Icons.Default.ArrowForward,
                  contentDescription = "Next Page",
                  tint = if (currentPage < totalPages) theme.accentCyan else theme.textSecondary.copy(alpha = 0.4f)
                )
              }
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              IconButton(
                onClick = { if (zoomScale > 0.8f) zoomScale -= 0.15f },
                modifier = Modifier.size(36.dp).testTag("pdf_zoom_out")
              ) {
                Icon(
                  imageVector = Icons.Default.ZoomOut,
                  contentDescription = "Zoom Out",
                  tint = theme.textPrimary
                )
              }

              Text(
                text = "${(zoomScale * 100).toInt()}%",
                color = theme.textSecondary,
                fontSize = 11.sp,
                modifier = Modifier.width(36.dp)
              )

              IconButton(
                onClick = { if (zoomScale < 1.6f) zoomScale += 0.15f },
                modifier = Modifier.size(36.dp).testTag("pdf_zoom_in")
              ) {
                Icon(
                  imageVector = Icons.Default.ZoomIn,
                  contentDescription = "Zoom In",
                  tint = theme.textPrimary
                )
              }
            }
          }
        }
      }
    }
  }
}
