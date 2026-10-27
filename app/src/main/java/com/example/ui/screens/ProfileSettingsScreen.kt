package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AuthRepository
import com.example.ui.components.GlassCard
import com.example.ui.theme.LocalNeoTheme
import com.example.ui.theme.NeoThemeId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Section 1, 2, 3: Rebuilt Clean Production Settings Screen
 * All backend, developer, and demo gateway controls are completely removed.
 * Contains only required user-facing options and a proper Logout confirmation dialog.
 */
@Composable
fun ProfileSettingsScreen(
  authRepository: AuthRepository,
  currentThemeId: NeoThemeId,
  onOpenThemeSelector: () -> Unit,
  coroutineScope: CoroutineScope,
  onLogout: () -> Unit,
  onShowToast: (String) -> Unit
) {
  val theme = LocalNeoTheme.current
  val currentUser by authRepository.currentUser.collectAsState()

  // Dialog States
  var showLogoutConfirmDialog by remember { mutableStateOf(false) }
  var showProfileDialog by remember { mutableStateOf(false) }
  var showNotificationsDialog by remember { mutableStateOf(false) }
  var showAboutDialog by remember { mutableStateOf(false) }
  var showPrivacyDialog by remember { mutableStateOf(false) }
  var showTermsDialog by remember { mutableStateOf(false) }

  // Profile Edit fields
  var editName by remember { mutableStateOf(currentUser?.name ?: "") }
  var editCollege by remember { mutableStateOf(currentUser?.college ?: "") }
  var editBranch by remember { mutableStateOf(currentUser?.branch ?: "") }
  var editSemester by remember { mutableStateOf(currentUser?.semester ?: "") }

  // Notification Preferences
  var notifAlertsEnabled by remember { mutableStateOf(true) }
  var notifReviewsEnabled by remember { mutableStateOf(true) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // Screen Title
    Column {
      Text(
        text = "SETTINGS",
        color = theme.accentCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
      )
      Text(
        text = "Preferences & Account",
        color = theme.textPrimary,
        fontSize = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = theme.fontFamily
      )
    }

    // ==========================================
    // 1. ACCOUNT SECTION
    // ==========================================
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "ACCOUNT",
        color = theme.accentCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )

      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        contentPadding = 6.dp
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Profile Item
          SettingsActionRow(
            icon = Icons.Default.Person,
            iconTint = theme.accentCyan,
            title = "Profile",
            subtitle = currentUser?.let { "${it.name} (${it.role})" } ?: "Manage your account information",
            onClick = {
              currentUser?.let {
                editName = it.name
                editCollege = it.college
                editBranch = it.branch
                editSemester = it.semester
                showProfileDialog = true
              }
            },
            testTag = "settings_profile_item"
          )

          Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(theme.glassBorder))

          // Notifications Preferences Item
          SettingsActionRow(
            icon = Icons.Default.Notifications,
            iconTint = theme.accentViolet,
            title = "Notifications",
            subtitle = "Notification preferences & alerts",
            onClick = { showNotificationsDialog = true },
            testTag = "settings_notifications_item"
          )

          Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(theme.glassBorder))

          // Appearance / Theme Item
          SettingsActionRow(
            icon = Icons.Default.ColorLens,
            iconTint = theme.accentMagenta,
            title = "Appearance",
            subtitle = "${currentThemeId.displayName} (${currentThemeId.badgeText})",
            onClick = onOpenThemeSelector,
            trailingText = "Switch",
            testTag = "settings_appearance_item"
          )
        }
      }
    }

    // ==========================================
    // 2. ABOUT SECTION
    // ==========================================
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "ABOUT",
        color = theme.accentCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )

      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        contentPadding = 6.dp
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          // About NEO NOTES Item
          SettingsActionRow(
            icon = Icons.Default.Info,
            iconTint = theme.accentCyan,
            title = "About NEO NOTES",
            subtitle = "Version 3.1.0 • Production Release",
            onClick = { showAboutDialog = true },
            testTag = "settings_about_item"
          )

          Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(theme.glassBorder))

          // Privacy Policy Item
          SettingsActionRow(
            icon = Icons.Default.Policy,
            iconTint = theme.accentViolet,
            title = "Privacy Policy",
            subtitle = "Data protection & cloud archive privacy",
            onClick = { showPrivacyDialog = true },
            testTag = "settings_privacy_item"
          )

          Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(theme.glassBorder))

          // Terms & Conditions Item
          SettingsActionRow(
            icon = Icons.Default.Description,
            iconTint = theme.accentMagenta,
            title = "Terms & Conditions",
            subtitle = "Academic integrity & usage terms",
            onClick = { showTermsDialog = true },
            testTag = "settings_terms_item"
          )
        }
      }
    }

    // ==========================================
    // 3. ACCOUNT ACTION — LOGOUT ONLY (Section 3)
    // ==========================================
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "ACCOUNT ACTION",
        color = theme.accentCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )

      Button(
        onClick = { showLogoutConfirmDialog = true },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0x33FF5252),
          contentColor = Color(0xFFFF5252)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("logout_btn")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Logout,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "LOG OUT",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(40.dp))
  }

  // ==========================================
  // CONFIRMATION & DETAIL DIALOGS
  // ==========================================

  // Section 3: Proper Logout Confirmation Dialog
  if (showLogoutConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirmDialog = false },
      title = {
        Text(
          text = "Log out of NEO NOTES?",
          color = theme.textPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        )
      },
      text = {
        Text(
          text = "You will need to log in again to access your account.",
          color = theme.textSecondary,
          fontSize = 14.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showLogoutConfirmDialog = false
            onLogout()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("confirm_logout_btn")
        ) {
          Text("Log Out", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        OutlinedButton(
          onClick = { showLogoutConfirmDialog = false },
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Cancel", color = theme.textPrimary)
        }
      },
      containerColor = theme.bgNavy,
      shape = RoundedCornerShape(18.dp)
    )
  }

  // Profile Dialog
  if (showProfileDialog) {
    AlertDialog(
      onDismissRequest = { showProfileDialog = false },
      title = {
        Text("Manage Profile", color = theme.textPrimary, fontWeight = FontWeight.Bold)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          currentUser?.let { user ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(theme.accentCyan.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("ROLE: ${user.role}", color = theme.accentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          OutlinedTextField(
            value = editName,
            onValueChange = { editName = it },
            label = { Text("Full Name") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = theme.accentCyan,
              focusedTextColor = theme.textPrimary,
              unfocusedTextColor = theme.textPrimary
            ),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = editCollege,
            onValueChange = { editCollege = it },
            label = { Text("College / University") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = theme.accentCyan,
              focusedTextColor = theme.textPrimary,
              unfocusedTextColor = theme.textPrimary
            ),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = editBranch,
            onValueChange = { editBranch = it },
            label = { Text("Branch / Department") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = theme.accentCyan,
              focusedTextColor = theme.textPrimary,
              unfocusedTextColor = theme.textPrimary
            ),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = editSemester,
            onValueChange = { editSemester = it },
            label = { Text("Semester / Designation") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = theme.accentCyan,
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
              authRepository.updateProfile(editName, editCollege, editBranch, editSemester)
              showProfileDialog = false
              onShowToast("Profile information updated!")
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = theme.accentCyan)
        ) {
          Text("Save Changes", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showProfileDialog = false }) {
          Text("Close", color = theme.textPrimary)
        }
      },
      containerColor = theme.bgNavy,
      shape = RoundedCornerShape(18.dp)
    )
  }

  // Notifications Dialog
  if (showNotificationsDialog) {
    AlertDialog(
      onDismissRequest = { showNotificationsDialog = false },
      title = { Text("Notification Preferences", color = theme.textPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Academic Alerts", color = theme.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
              Text("Get notified when new course notes are published", color = theme.textSecondary, fontSize = 11.sp)
            }
            Switch(
              checked = notifAlertsEnabled,
              onCheckedChange = { notifAlertsEnabled = it },
              colors = SwitchDefaults.colors(checkedThumbColor = theme.accentCyan)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Review & Approval Status", color = theme.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
              Text("Receive updates when uploaded notes are approved", color = theme.textSecondary, fontSize = 11.sp)
            }
            Switch(
              checked = notifReviewsEnabled,
              onCheckedChange = { notifReviewsEnabled = it },
              colors = SwitchDefaults.colors(checkedThumbColor = theme.accentCyan)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showNotificationsDialog = false
            onShowToast("Notification preferences updated")
          },
          colors = ButtonDefaults.buttonColors(containerColor = theme.accentCyan)
        ) {
          Text("Done")
        }
      },
      containerColor = theme.bgNavy,
      shape = RoundedCornerShape(18.dp)
    )
  }

  // About Dialog
  if (showAboutDialog) {
    AlertDialog(
      onDismissRequest = { showAboutDialog = false },
      title = { Text("About NEO NOTES", color = theme.textPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Version 3.1.0 (Production Build)", color = theme.accentCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(
            "NEO NOTES is a modern academic notes sharing and document archival platform designed for college engineering faculties and students.",
            color = theme.textSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "Powered by NitroFlare cloud document storage with peer-reviewed academic curation.",
            color = theme.textSecondary,
            fontSize = 12.sp
          )
        }
      },
      confirmButton = {
        Button(onClick = { showAboutDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = theme.accentCyan)) {
          Text("Close")
        }
      },
      containerColor = theme.bgNavy,
      shape = RoundedCornerShape(18.dp)
    )
  }

  // Privacy Dialog
  if (showPrivacyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyDialog = false },
      title = { Text("Privacy Policy", color = theme.textPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Academic Data Protection", color = theme.accentCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(
            "NEO NOTES respects the privacy of students and professors. Your personal login credentials and account tokens are secured locally on your device and verified with the institutional gateway.",
            color = theme.textSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp
          )
          Text(
            "Documents contributed to the campus repository are stored in cloud archives for academic non-commercial access.",
            color = theme.textSecondary,
            fontSize = 12.sp
          )
        }
      },
      confirmButton = {
        Button(onClick = { showPrivacyDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = theme.accentCyan)) {
          Text("Understood")
        }
      },
      containerColor = theme.bgNavy,
      shape = RoundedCornerShape(18.dp)
    )
  }

  // Terms Dialog
  if (showTermsDialog) {
    AlertDialog(
      onDismissRequest = { showTermsDialog = false },
      title = { Text("Terms & Conditions", color = theme.textPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Academic Integrity & Conduct", color = theme.accentCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(
            "By contributing documents to NEO NOTES, you confirm that your study materials adhere to academic integrity standards and do not infringe copyrighted textbook publications.",
            color = theme.textSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp
          )
        }
      },
      confirmButton = {
        Button(onClick = { showTermsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = theme.accentCyan)) {
          Text("Accept")
        }
      },
      containerColor = theme.bgNavy,
      shape = RoundedCornerShape(18.dp)
    )
  }
}

@Composable
private fun SettingsActionRow(
  icon: ImageVector,
  iconTint: Color,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  trailingText: String? = null,
  testTag: String = ""
) {
  val theme = LocalNeoTheme.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 12.dp)
      .testTag(testTag),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(iconTint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(
          text = title,
          color = theme.textPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = subtitle,
          color = theme.textSecondary,
          fontSize = 12.sp
        )
      }
    }

    if (trailingText != null) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(theme.accentCyan.copy(alpha = 0.15f))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Text(
          text = trailingText,
          color = theme.accentCyan,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    } else {
      Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = null,
        tint = theme.textSecondary.copy(alpha = 0.6f),
        modifier = Modifier.size(18.dp)
      )
    }
  }
}
