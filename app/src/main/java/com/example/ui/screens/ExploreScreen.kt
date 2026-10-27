package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.SeedData
import com.example.data.model.Note
import com.example.data.repository.AuthRepository
import com.example.data.repository.NotesRepository
import com.example.ui.components.DocumentViewerModal
import com.example.ui.components.FilterChipRow
import com.example.ui.components.GlassCard
import com.example.ui.components.NeoSearchBar
import com.example.ui.components.NoteCard
import com.example.ui.components.SortOption
import com.example.ui.theme.LocalNeoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ExploreScreen(
  notesRepository: NotesRepository,
  authRepository: AuthRepository,
  coroutineScope: CoroutineScope,
  onNavigateToUpload: () -> Unit,
  onShowToast: (String) -> Unit
) {
  val theme = LocalNeoTheme.current
  val currentUser by authRepository.currentUser.collectAsState()

  val allApprovedNotes by notesRepository.allApprovedNotes.collectAsState(initial = emptyList())
  val bookmarkedNotes by currentUser?.let {
    notesRepository.getBookmarkedNotes(it.id).collectAsState(initial = emptyList())
  } ?: remember { mutableStateOf(emptyList()) }

  val bookmarkedIds by remember {
    derivedStateOf { bookmarkedNotes.map { it.id }.toSet() }
  }

  // Filters State
  var searchQuery by remember { mutableStateOf("") }
  var selectedSubject by remember { mutableStateOf("All") }
  var selectedBranch by remember { mutableStateOf("All") }
  var selectedSemester by remember { mutableStateOf("All") }
  var selectedFileType by remember { mutableStateOf("All") }
  var selectedSort by remember { mutableStateOf(SortOption.NEWEST.label) }

  // Document Preview State
  var previewNote by remember { mutableStateOf<Note?>(null) }

  // Filtered Notes
  val filteredNotes by remember {
    derivedStateOf {
      allApprovedNotes
        .filter { note ->
          if (searchQuery.isBlank()) true
          else {
            val q = searchQuery.trim().lowercase()
            note.title.lowercase().contains(q) ||
                note.description.lowercase().contains(q) ||
                note.subject.lowercase().contains(q) ||
                note.branch.lowercase().contains(q) ||
                note.tags.lowercase().contains(q) ||
                note.uploaderName.lowercase().contains(q)
          }
        }
        .filter { selectedSubject == "All" || it.subject.equals(selectedSubject, ignoreCase = true) }
        .filter { selectedBranch == "All" || it.branch.equals(selectedBranch, ignoreCase = true) }
        .filter { selectedSemester == "All" || it.semester.equals(selectedSemester, ignoreCase = true) }
        .filter { selectedFileType == "All" || it.fileType.equals(selectedFileType, ignoreCase = true) }
        .sortedWith { a, b ->
          when (selectedSort) {
            SortOption.MOST_DOWNLOADED.label -> b.downloadCount.compareTo(a.downloadCount)
            SortOption.MOST_BOOKMARKED.label -> b.bookmarkCount.compareTo(a.bookmarkCount)
            SortOption.ALPHABETICAL.label -> a.title.compareTo(b.title, ignoreCase = true)
            else -> b.createdAt.compareTo(a.createdAt)
          }
        }
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("explore_notes_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. HERO SECTION
      item {
        GlassCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(22.dp),
          borderGlow = true,
          contentPadding = 0.dp
        ) {
          Column {
            // Hero Banner Image
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
            ) {
              Image(
                painter = painterResource(id = R.drawable.neo_hero_banner_1790175976958),
                contentDescription = "Campus Library",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
              // Gradient Overlay
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(Color.Transparent, theme.bgNavy.copy(alpha = 0.95f))
                    )
                  )
              )
            }

            // Hero Texts & CTAs
            Column(modifier = Modifier.padding(18.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = theme.accentCyan,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "ACADEMIC ARCHIVE 3.0",
                  color = theme.accentCyan,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.2.sp
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = "Your Knowledge.\nOne Shared Space.",
                color = theme.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 28.sp,
                fontFamily = theme.fontFamily
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "Discover, share and organize verified college notes with a futuristic learning platform.",
                color = theme.textSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
              )

              Spacer(modifier = Modifier.height(16.dp))

              // CTA Buttons
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Button(
                  onClick = onNavigateToUpload,
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = theme.accentCyan,
                    contentColor = if (theme.isDark) Color(0xFF080A18) else Color.White
                  ),
                  modifier = Modifier.weight(1f).testTag("hero_upload_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Share Notes", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                  onClick = { selectedSubject = "Data Structures" },
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.weight(1f).testTag("hero_explore_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = null,
                    tint = theme.accentCyan,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Top Topics", color = theme.accentCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
              }
            }
          }
        }
      }

      // 2. STATS ROW
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatCapsule(label = "Notes", count = "10K+", icon = Icons.Default.School, modifier = Modifier.weight(1f))
          StatCapsule(label = "Students", count = "5K+", icon = Icons.Default.TrendingUp, modifier = Modifier.weight(1f))
          StatCapsule(label = "Subjects", count = "50+", icon = Icons.Default.Explore, modifier = Modifier.weight(1f))
          StatCapsule(label = "Branches", count = "25+", icon = Icons.Default.AutoAwesome, modifier = Modifier.weight(1f))
        }
      }

      // 3. SEARCH BAR
      item {
        NeoSearchBar(
          query = searchQuery,
          onQueryChange = { searchQuery = it },
          placeholder = "Search notes, subjects, topics, tags..."
        )
      }

      // 4. FILTER CHIPS
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Subject filter
          val subjectOptions = listOf("All") + SeedData.subjectsList
          FilterChipRow(
            options = subjectOptions,
            selectedOption = selectedSubject,
            onOptionSelected = { selectedSubject = it },
            label = "SUBJECT"
          )

          // Branch filter
          val branchOptions = listOf("All") + SeedData.branchesList
          FilterChipRow(
            options = branchOptions,
            selectedOption = selectedBranch,
            onOptionSelected = { selectedBranch = it },
            label = "BRANCH"
          )

          // File Type & Sort Options
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Column(modifier = Modifier.weight(1f)) {
              FilterChipRow(
                options = listOf("All", "PDF", "DOCX", "PPTX"),
                selectedOption = selectedFileType,
                onOptionSelected = { selectedFileType = it },
                label = "FORMAT"
              )
            }

            Column(modifier = Modifier.weight(1.3f)) {
              FilterChipRow(
                options = listOf(
                  SortOption.NEWEST.label,
                  SortOption.MOST_DOWNLOADED.label,
                  SortOption.MOST_BOOKMARKED.label,
                  SortOption.ALPHABETICAL.label
                ),
                selectedOption = selectedSort,
                onOptionSelected = { selectedSort = it },
                label = "SORT"
              )
            }
          }
        }
      }

      // 5. RESULTS COUNT
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "VERIFIED STUDY MATERIALS (${filteredNotes.size})",
            color = theme.accentCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )

          if (selectedSubject != "All" || selectedBranch != "All" || searchQuery.isNotEmpty()) {
            Text(
              text = "Reset Filters",
              color = theme.accentMagenta,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier
                .clickable {
                  searchQuery = ""
                  selectedSubject = "All"
                  selectedBranch = "All"
                  selectedSemester = "All"
                  selectedFileType = "All"
                }
                .testTag("reset_filters_btn")
            )
          }
        }
      }

      // 6. EMPTY STATE OR NOTES LIST
      if (filteredNotes.isEmpty()) {
        item {
          GlassCard(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 20.dp),
            contentPadding = 32.dp
          ) {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .clip(CircleShape)
                  .background(theme.accentCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.FilterList,
                  contentDescription = null,
                  tint = theme.accentCyan,
                  modifier = Modifier.size(28.dp)
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = "No matching notes found",
                color = theme.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = theme.fontFamily
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "Try adjusting your search query, selecting another branch, or clearing filters.",
                color = theme.textSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
              )
            }
          }
        }
      } else {
        items(filteredNotes, key = { it.id }) { note ->
          val isBookmarked = note.id in bookmarkedIds

          NoteCard(
            note = note,
            isBookmarked = isBookmarked,
            onView = { previewNote = note },
            onDownload = {
              notesRepository.storageManager.downloadToDevice(note.effectiveUrl, note.fileName)
              currentUser?.let { user ->
                coroutineScope.launch {
                  notesRepository.recordDownload(note.id, user.id)
                }
              }
            },
            onToggleBookmark = {
              currentUser?.let { user ->
                coroutineScope.launch {
                  notesRepository.toggleBookmark(note.id, user.id)
                  val msg = if (isBookmarked) "Removed from bookmarks" else "Saved to bookmarks!"
                  onShowToast(msg)
                }
              } ?: onShowToast("Please log in to bookmark notes.")
            }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(60.dp))
      }
    }

    // Document Preview Modal
    previewNote?.let { note ->
      DocumentViewerModal(
        note = note,
        onDismiss = { previewNote = null },
        onDownload = {
          notesRepository.storageManager.downloadToDevice(note.effectiveUrl, note.fileName)
          currentUser?.let { user ->
            coroutineScope.launch {
              notesRepository.recordDownload(note.id, user.id)
            }
          }
        }
      )
    }
  }
}

@Composable
private fun StatCapsule(
  label: String,
  count: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier
) {
  val theme = LocalNeoTheme.current
  GlassCard(
    modifier = modifier,
    contentPadding = 8.dp,
    shape = RoundedCornerShape(14.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = count,
        color = theme.accentCyan,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = theme.fontFamily
      )
      Text(
        text = label,
        color = theme.textSecondary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
