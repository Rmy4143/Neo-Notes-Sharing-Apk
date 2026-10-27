package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.repository.AuthRepository
import com.example.data.repository.NotesRepository
import com.example.data.repository.NotificationRepository
import com.example.data.storage.StorageManager
import com.example.ui.components.BackgroundMesh
import com.example.ui.components.GlassCard
import com.example.ui.components.ThemeBadge
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.components.ThemeTransitionRequest
import com.example.ui.components.WaterWaveTransitionOverlay
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.NotificationsDialog
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.UploadScreen
import com.example.ui.theme.LocalNeoTheme
import com.example.ui.theme.NeoNotesTheme
import com.example.ui.theme.NeoThemeId
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AppScreen {
  EXPLORE,
  UPLOAD,
  DASHBOARD,
  ADMIN,
  PROFILE,
  AUTH
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      NeoNotesRoot()
    }
  }
}

@Composable
fun NeoNotesRoot() {
  val scope = rememberCoroutineScope()
  val context = androidx.compose.ui.platform.LocalContext.current

  // Initialize DB & Repositories
  val database = remember { AppDatabase.getDatabase(context, scope) }
  val storageManager = remember { StorageManager(context) }
  val authRepository = remember { AuthRepository(context, database, scope) }
  val notesRepository = remember { NotesRepository(context, database, storageManager) }
  val notificationRepository = remember { NotificationRepository(database) }

  // Theme & Water-wave transition state
  var currentThemeId by remember { mutableStateOf(NeoThemeId.NEON) }
  var themeTransitionRequest by remember { mutableStateOf<ThemeTransitionRequest?>(null) }
  var prefersReducedMotion by remember { mutableStateOf(false) }

  // App Navigation State - defaults to DASHBOARD when authenticated
  var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }

  // Dialogs
  var showThemeSelector by remember { mutableStateOf(false) }
  var showNotifications by remember { mutableStateOf(false) }

  // Toast / Status banner
  var toastMessage by remember { mutableStateOf<String?>(null) }

  fun showToast(msg: String) {
    toastMessage = msg
  }

  LaunchedEffect(toastMessage) {
    if (toastMessage != null) {
      delay(3200)
      toastMessage = null
    }
  }

  val isSessionLoaded by authRepository.isSessionLoaded.collectAsState()
  val currentUser by authRepository.currentUser.collectAsState()
  val unreadNotifCount by currentUser?.let {
    notificationRepository.getUnreadCount(it.id).collectAsState(initial = 0)
  } ?: remember { mutableStateOf(0) }

  NeoNotesTheme(themeId = currentThemeId) {
    val theme = LocalNeoTheme.current

    Box(modifier = Modifier.fillMaxSize()) {
      BackgroundMesh {
        if (!isSessionLoaded) {
          // Loading splash while checking persisted session
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              CircularProgressIndicator(
                color = theme.accentCyan,
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
              )
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                text = "NEO NOTES",
                color = theme.textPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                letterSpacing = 1.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Checking session...",
                color = theme.textSecondary,
                fontSize = 12.sp
              )
            }
          }
        } else if (currentUser == null) {
          // Section 4: Login screen is the entry point when unauthenticated
          AuthScreen(
            authRepository = authRepository,
            coroutineScope = scope,
            onAuthSuccess = {
              currentScreen = AppScreen.DASHBOARD
            },
            onShowToast = ::showToast
          )
        } else {
          // Authenticated User Experience
          Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
              // Top App Bar with Edge-to-Edge inset
              val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = statusBarHeight)
                  .background(theme.bgNavy.copy(alpha = 0.85f))
                  .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                      listOf(theme.glassBorder.copy(alpha = 0.2f), theme.glassBorder.copy(alpha = 0.05f))
                    ),
                    shape = RectangleShape
                  )
                  .padding(horizontal = 16.dp, vertical = 10.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  // Logo & Brand
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                      .clickable { currentScreen = AppScreen.DASHBOARD }
                      .testTag("app_logo_btn")
                  ) {
                    Box(
                      modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.accentCyan.copy(alpha = 0.2f))
                        .border(1.dp, theme.accentCyan, RoundedCornerShape(8.dp)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Neo Notes",
                        tint = theme.accentCyan,
                        modifier = Modifier.size(18.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Text(
                        text = "NEO NOTES",
                        color = theme.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = theme.fontFamily,
                        letterSpacing = 1.sp
                      )
                      Text(
                        text = "CAMPUS REPOSITORY",
                        color = theme.accentCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                      )
                    }
                  }

                  // Top Actions: Theme Badge + Notifications + Profile
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    // Theme Badge with pulsing animation
                    ThemeBadge(
                      onClick = { showThemeSelector = true }
                    )

                    // Notifications Icon with real-time badge
                    IconButton(
                      onClick = { showNotifications = true },
                      modifier = Modifier
                        .size(36.dp)
                        .testTag("top_notifications_btn")
                    ) {
                      BadgedBox(
                        badge = {
                          if (unreadNotifCount > 0) {
                            Badge(
                              containerColor = theme.accentCyan,
                              contentColor = Color.Black
                            ) {
                              Text("$unreadNotifCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                          }
                        }
                      ) {
                        Icon(
                          imageVector = Icons.Default.Notifications,
                          contentDescription = "Notifications",
                          tint = theme.textPrimary,
                          modifier = Modifier.size(20.dp)
                        )
                      }
                    }

                    // Profile Avatar
                    Box(
                      modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(theme.glassBg)
                        .border(1.dp, theme.accentCyan.copy(alpha = 0.5f), CircleShape)
                        .clickable { currentScreen = AppScreen.PROFILE }
                        .testTag("top_profile_btn"),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = currentUser!!.name.take(2).uppercase(),
                        color = theme.accentCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }
            },
            bottomBar = {
              // Glassmorphic Bottom Navigation Bar with insets
              val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
              val isFaculty = currentUser?.role == "TEACHER" || currentUser?.role == "ADMIN"
              NavigationBar(
                containerColor = theme.bgNavy.copy(alpha = 0.95f),
                tonalElevation = 8.dp,
                modifier = Modifier
                  .fillMaxWidth()
                  .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                      listOf(theme.glassBorder.copy(alpha = 0.35f), Color.Transparent)
                    ),
                    shape = RectangleShape
                  )
                  .padding(bottom = navBarPadding)
              ) {
                // 1. Dashboard ("My Notes")
                NavigationBarItem(
                  selected = currentScreen == AppScreen.DASHBOARD,
                  onClick = { currentScreen = AppScreen.DASHBOARD },
                  icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                  label = { Text("My Notes", fontSize = 11.sp) },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = theme.accentCyan,
                    selectedTextColor = theme.accentCyan,
                    unselectedIconColor = theme.textSecondary,
                    unselectedTextColor = theme.textSecondary,
                    indicatorColor = theme.accentCyan.copy(alpha = 0.15f)
                  ),
                  modifier = Modifier.testTag("nav_dashboard")
                )

                // 2. Explore
                NavigationBarItem(
                  selected = currentScreen == AppScreen.EXPLORE,
                  onClick = { currentScreen = AppScreen.EXPLORE },
                  icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                  label = { Text("Explore", fontSize = 11.sp) },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = theme.accentCyan,
                    selectedTextColor = theme.accentCyan,
                    unselectedIconColor = theme.textSecondary,
                    unselectedTextColor = theme.textSecondary,
                    indicatorColor = theme.accentCyan.copy(alpha = 0.15f)
                  ),
                  modifier = Modifier.testTag("nav_explore")
                )

                // 3. Upload
                NavigationBarItem(
                  selected = currentScreen == AppScreen.UPLOAD,
                  onClick = { currentScreen = AppScreen.UPLOAD },
                  icon = { Icon(Icons.Default.CloudUpload, contentDescription = "Upload") },
                  label = { Text("Upload", fontSize = 11.sp) },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = theme.accentCyan,
                    selectedTextColor = theme.accentCyan,
                    unselectedIconColor = theme.textSecondary,
                    unselectedTextColor = theme.textSecondary,
                    indicatorColor = theme.accentCyan.copy(alpha = 0.15f)
                  ),
                  modifier = Modifier.testTag("nav_upload")
                )

                // 4. Faculty Review (Only for Teachers / Admins)
                if (isFaculty) {
                  NavigationBarItem(
                    selected = currentScreen == AppScreen.ADMIN,
                    onClick = { currentScreen = AppScreen.ADMIN },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Review") },
                    label = { Text("Review", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = theme.accentMagenta,
                      selectedTextColor = theme.accentMagenta,
                      unselectedIconColor = theme.textSecondary,
                      unselectedTextColor = theme.textSecondary,
                      indicatorColor = theme.accentMagenta.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_admin")
                  )
                }

                // 5. Settings
                NavigationBarItem(
                  selected = currentScreen == AppScreen.PROFILE,
                  onClick = { currentScreen = AppScreen.PROFILE },
                  icon = { Icon(Icons.Default.Tune, contentDescription = "Settings") },
                  label = { Text("Settings", fontSize = 11.sp) },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = theme.accentCyan,
                    selectedTextColor = theme.accentCyan,
                    unselectedIconColor = theme.textSecondary,
                    unselectedTextColor = theme.textSecondary,
                    indicatorColor = theme.accentCyan.copy(alpha = 0.15f)
                  ),
                  modifier = Modifier.testTag("nav_settings")
                )
              }
            }
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
              when (currentScreen) {
                AppScreen.DASHBOARD -> {
                  DashboardScreen(
                    notesRepository = notesRepository,
                    authRepository = authRepository,
                    coroutineScope = scope,
                    onNavigateToUpload = { currentScreen = AppScreen.UPLOAD },
                    onShowToast = ::showToast
                  )
                }
                AppScreen.EXPLORE -> {
                  ExploreScreen(
                    notesRepository = notesRepository,
                    authRepository = authRepository,
                    coroutineScope = scope,
                    onNavigateToUpload = { currentScreen = AppScreen.UPLOAD },
                    onShowToast = ::showToast
                  )
                }
                AppScreen.UPLOAD -> {
                  UploadScreen(
                    notesRepository = notesRepository,
                    authRepository = authRepository,
                    coroutineScope = scope,
                    onNavigateToDashboard = { currentScreen = AppScreen.DASHBOARD },
                    onShowToast = ::showToast
                  )
                }
                AppScreen.ADMIN -> {
                  AdminScreen(
                    notesRepository = notesRepository,
                    authRepository = authRepository,
                    coroutineScope = scope,
                    onShowToast = ::showToast
                  )
                }
                AppScreen.PROFILE -> {
                  ProfileSettingsScreen(
                    authRepository = authRepository,
                    currentThemeId = currentThemeId,
                    onOpenThemeSelector = { showThemeSelector = true },
                    coroutineScope = scope,
                    onLogout = {
                      authRepository.logout()
                      showToast("You have been logged out.")
                    },
                    onShowToast = ::showToast
                  )
                }
                AppScreen.AUTH -> {
                  // Fallback if explicitly navigated
                  AuthScreen(
                    authRepository = authRepository,
                    coroutineScope = scope,
                    onAuthSuccess = { currentScreen = AppScreen.DASHBOARD },
                    onShowToast = ::showToast
                  )
                }
              }
            }
          }
        }
      }

      // 1. Toast / Notification Overlay Banner
      AnimatedVisibility(
        visible = toastMessage != null,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 86.dp, start = 16.dp, end = 16.dp)
      ) {
        toastMessage?.let { msg ->
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderGlow = true,
            glowColor = theme.accentCyan,
            shape = RoundedCornerShape(14.dp),
            contentPadding = 12.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(theme.accentCyan)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = msg,
                color = theme.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }

      // 2. CINEMATIC WATER-WAVE TRANSITION OVERLAY (Section 9)
      WaterWaveTransitionOverlay(
        activeRequest = themeTransitionRequest,
        reducedMotion = prefersReducedMotion,
        onApplyTheme = { newThemeId ->
          currentThemeId = newThemeId
        },
        onTransitionFinished = {
          themeTransitionRequest = null
        }
      )

      // 3. Theme Selector Dialog
      if (showThemeSelector) {
        ThemeSelectorDialog(
          currentThemeId = currentThemeId,
          onThemeSelected = { newThemeId, clickCoords ->
            showThemeSelector = false
            themeTransitionRequest = ThemeTransitionRequest(
              targetTheme = newThemeId,
              origin = clickCoords
            )
          },
          onDismiss = { showThemeSelector = false }
        )
      }

      // 4. Notifications Dialog
      if (showNotifications && currentUser != null) {
        NotificationsDialog(
          userId = currentUser!!.id,
          notificationRepository = notificationRepository,
          coroutineScope = scope,
          onDismiss = { showNotifications = false }
        )
      }
    }
  }
}
