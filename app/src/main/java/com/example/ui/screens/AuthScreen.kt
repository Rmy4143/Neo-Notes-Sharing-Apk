package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AuthRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.OtpVerificationComponent
import com.example.ui.theme.LocalNeoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class AuthStep {
  ROLE_SELECT,
  STUDENT_LOGIN,
  STUDENT_SIGNUP,
  TEACHER_LOGIN
}

/**
 * Section 4, 5, 6, 7:
 * Secure Login Entry Point with Role Selection (Student vs Teacher).
 * No Admin switch. No demo buttons. No Teacher signup.
 */
@Composable
fun AuthScreen(
  authRepository: AuthRepository,
  coroutineScope: CoroutineScope,
  onAuthSuccess: () -> Unit,
  onShowToast: (String) -> Unit
) {
  val theme = LocalNeoTheme.current
  var currentStep by remember { mutableStateOf(AuthStep.ROLE_SELECT) }

  // Student inputs
  var studentEmail by remember { mutableStateOf("") }
  var studentPassword by remember { mutableStateOf("") }
  var studentPasswordVisible by remember { mutableStateOf(false) }
  var isStudentLoginEmailVerified by remember { mutableStateOf(false) }

  // Student Signup inputs
  var regName by remember { mutableStateOf("") }
  var regEmail by remember { mutableStateOf("") }
  var regPassword by remember { mutableStateOf("") }
  var regConfirmPassword by remember { mutableStateOf("") }
  var regPasswordVisible by remember { mutableStateOf(false) }
  var regCollege by remember { mutableStateOf("") }
  var regBranch by remember { mutableStateOf("") }
  var regSemester by remember { mutableStateOf("Semester 4") }
  var isStudentRegEmailVerified by remember { mutableStateOf(false) }

  // Teacher inputs
  var teacherIdentifier by remember { mutableStateOf("") }
  var teacherPassword by remember { mutableStateOf("") }
  var teacherPasswordVisible by remember { mutableStateOf(false) }

  // State
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    GlassCard(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState()),
      shape = RoundedCornerShape(24.dp),
      borderGlow = true
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // App Logo / Brand Header
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(theme.accentCyan.copy(alpha = 0.2f))
            .border(1.5.dp, theme.accentCyan, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = theme.accentCyan,
            modifier = Modifier.size(28.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "NEO NOTES",
          color = theme.textPrimary,
          fontSize = 22.sp,
          fontWeight = FontWeight.ExtraBold,
          fontFamily = theme.fontFamily,
          letterSpacing = 1.2.sp
        )

        Text(
          text = "Campus Repository",
          color = theme.accentCyan,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Error message banner
        errorMessage?.let { msg ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0x33FF5252))
              .border(1.dp, Color(0x66FF5252), RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            Text(text = msg, color = Color(0xFFFF8A80), fontSize = 12.sp)
          }
          Spacer(modifier = Modifier.height(14.dp))
        }

        AnimatedContent(
          targetState = currentStep,
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "auth_step_transition"
        ) { step ->
          when (step) {
            AuthStep.ROLE_SELECT -> {
              // Section 5: Exact 2 Role Options: Student Login vs Teacher Login
              Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "Select your account type to proceed:",
                  color = theme.textSecondary,
                  fontSize = 13.sp,
                  textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // STUDENT LOGIN BUTTON
                Button(
                  onClick = {
                    errorMessage = null
                    currentStep = AuthStep.STUDENT_LOGIN
                  },
                  shape = RoundedCornerShape(14.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = theme.accentCyan,
                    contentColor = if (theme.isDark) Color(0xFF080A18) else Color.White
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("select_student_login_btn")
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.School,
                      contentDescription = null,
                      modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = "STUDENT LOGIN",
                      fontWeight = FontWeight.ExtraBold,
                      fontSize = 15.sp,
                      letterSpacing = 0.5.sp
                    )
                  }
                }

                // TEACHER LOGIN BUTTON
                Button(
                  onClick = {
                    errorMessage = null
                    currentStep = AuthStep.TEACHER_LOGIN
                  },
                  shape = RoundedCornerShape(14.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = theme.accentViolet,
                    contentColor = Color.White
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("select_teacher_login_btn")
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.SupervisorAccount,
                      contentDescription = null,
                      modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = "TEACHER LOGIN",
                      fontWeight = FontWeight.ExtraBold,
                      fontSize = 15.sp,
                      letterSpacing = 0.5.sp
                    )
                  }
                }
              }
            }

            AuthStep.STUDENT_LOGIN -> {
              // Section 6: Student Login
              Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  IconButton(
                    onClick = {
                      errorMessage = null
                      currentStep = AuthStep.ROLE_SELECT
                    }
                  ) {
                    Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                      contentDescription = "Back",
                      tint = theme.accentCyan
                    )
                  }
                  Text(
                    text = "Student Login",
                    color = theme.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = theme.fontFamily
                  )
                }

                OutlinedTextField(
                  value = studentEmail,
                  onValueChange = {
                    studentEmail = it
                    isStudentLoginEmailVerified = false
                  },
                  label = { Text("Student Email") },
                  placeholder = { Text("e.g. alex.vance@campus.edu") },
                  leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = theme.accentCyan) },
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.accentCyan,
                    unfocusedBorderColor = theme.glassBorder,
                    focusedTextColor = theme.textPrimary,
                    unfocusedTextColor = theme.textPrimary
                  ),
                  modifier = Modifier.fillMaxWidth().testTag("student_email_input")
                )

                // Section 2 & 3: Email Verification directly below Email field
                OtpVerificationComponent(
                  email = studentEmail,
                  isVerified = isStudentLoginEmailVerified,
                  onVerified = { isStudentLoginEmailVerified = true },
                  authRepository = authRepository,
                  coroutineScope = coroutineScope,
                  onShowToast = onShowToast
                )

                OutlinedTextField(
                  value = studentPassword,
                  onValueChange = { studentPassword = it },
                  label = { Text("Password") },
                  leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = theme.accentCyan) },
                  trailingIcon = {
                    IconButton(onClick = { studentPasswordVisible = !studentPasswordVisible }) {
                      Icon(
                        imageVector = if (studentPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = theme.textSecondary
                      )
                    }
                  },
                  visualTransformation = if (studentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.accentCyan,
                    unfocusedBorderColor = theme.glassBorder,
                    focusedTextColor = theme.textPrimary,
                    unfocusedTextColor = theme.textPrimary
                  ),
                  modifier = Modifier.fillMaxWidth().testTag("student_password_input")
                )

                Button(
                  onClick = {
                    if (studentEmail.isBlank() || studentPassword.isBlank()) {
                      errorMessage = "Please enter your student email and password."
                      return@Button
                    }
                    isLoading = true
                    coroutineScope.launch {
                      val isAlreadyVerified = isStudentLoginEmailVerified || authRepository.isStudentEmailVerified(studentEmail)
                      if (!isAlreadyVerified) {
                        isLoading = false
                        errorMessage = "Please verify your student email with the verification code sent via Brevo."
                        return@launch
                      }

                      val res = authRepository.loginStudent(studentEmail, studentPassword)
                      isLoading = false
                      res.onSuccess {
                        onShowToast("Welcome back, ${it.name}!")
                        onAuthSuccess()
                      }.onFailure {
                        errorMessage = it.message ?: "Authentication failed."
                      }
                    }
                  },
                  enabled = !isLoading,
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = theme.accentCyan,
                    contentColor = if (theme.isDark) Color(0xFF080A18) else Color.White
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("student_login_submit_btn")
                ) {
                  if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                  } else {
                    Text("LOGIN", fontWeight = FontWeight.Bold)
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Don't have an account?", color = theme.textSecondary, fontSize = 12.sp)
                  TextButton(
                    onClick = {
                      errorMessage = null
                      currentStep = AuthStep.STUDENT_SIGNUP
                    },
                    modifier = Modifier.testTag("go_to_student_signup_btn")
                  ) {
                    Text("CREATE STUDENT ACCOUNT", color = theme.accentCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  }
                }
              }
            }

            AuthStep.STUDENT_SIGNUP -> {
              // Section 6: Student Signup Only
              Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  IconButton(
                    onClick = {
                      errorMessage = null
                      currentStep = AuthStep.STUDENT_LOGIN
                    }
                  ) {
                    Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                      contentDescription = "Back",
                      tint = theme.accentCyan
                    )
                  }
                  Text(
                    text = "Create Student Account",
                    color = theme.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = theme.fontFamily
                  )
                }

                OutlinedTextField(
                  value = regName,
                  onValueChange = { regName = it },
                  label = { Text("Full Name *") },
                  leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = theme.accentCyan) },
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.accentCyan,
                    unfocusedBorderColor = theme.glassBorder,
                    focusedTextColor = theme.textPrimary,
                    unfocusedTextColor = theme.textPrimary
                  ),
                  modifier = Modifier.fillMaxWidth().testTag("reg_name_input")
                )

                OutlinedTextField(
                  value = regEmail,
                  onValueChange = {
                    regEmail = it
                    isStudentRegEmailVerified = false
                  },
                  label = { Text("Student College Email *") },
                  leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = theme.accentCyan) },
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.accentCyan,
                    unfocusedBorderColor = theme.glassBorder,
                    focusedTextColor = theme.textPrimary,
                    unfocusedTextColor = theme.textPrimary
                  ),
                  modifier = Modifier.fillMaxWidth().testTag("reg_email_input")
                )

                // Section 16: Email Verification for Student Registration
                OtpVerificationComponent(
                  email = regEmail,
                  isVerified = isStudentRegEmailVerified,
                  onVerified = { isStudentRegEmailVerified = true },
                  authRepository = authRepository,
                  coroutineScope = coroutineScope,
                  onShowToast = onShowToast
                )

                OutlinedTextField(
                  value = regPassword,
                  onValueChange = { regPassword = it },
                  label = { Text("Password *") },
                  leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = theme.accentCyan) },
                  trailingIcon = {
                    IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                      Icon(
                        imageVector = if (regPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = theme.textSecondary
                      )
                    }
                  },
                  visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.accentCyan,
                    unfocusedBorderColor = theme.glassBorder,
                    focusedTextColor = theme.textPrimary,
                    unfocusedTextColor = theme.textPrimary
                  ),
                  modifier = Modifier.fillMaxWidth().testTag("reg_password_input")
                )

                OutlinedTextField(
                  value = regConfirmPassword,
                  onValueChange = { regConfirmPassword = it },
                  label = { Text("Confirm Password *") },
                  visualTransformation = PasswordVisualTransformation(),
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.accentCyan,
                    unfocusedBorderColor = theme.glassBorder,
                    focusedTextColor = theme.textPrimary,
                    unfocusedTextColor = theme.textPrimary
                  ),
                  modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                  value = regCollege,
                  onValueChange = { regCollege = it },
                  label = { Text("College / University") },
                  placeholder = { Text("e.g. Neo Institute of Tech") },
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.accentCyan,
                    unfocusedBorderColor = theme.glassBorder,
                    focusedTextColor = theme.textPrimary,
                    unfocusedTextColor = theme.textPrimary
                  ),
                  modifier = Modifier.fillMaxWidth()
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedTextField(
                    value = regBranch,
                    onValueChange = { regBranch = it },
                    label = { Text("Branch") },
                    placeholder = { Text("Computer Eng") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = theme.accentCyan,
                      unfocusedBorderColor = theme.glassBorder,
                      focusedTextColor = theme.textPrimary,
                      unfocusedTextColor = theme.textPrimary
                    ),
                    modifier = Modifier.weight(1f)
                  )

                  OutlinedTextField(
                    value = regSemester,
                    onValueChange = { regSemester = it },
                    label = { Text("Semester") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = theme.accentCyan,
                      unfocusedBorderColor = theme.glassBorder,
                      focusedTextColor = theme.textPrimary,
                      unfocusedTextColor = theme.textPrimary
                    ),
                    modifier = Modifier.weight(1f)
                  )
                }

                Button(
                  onClick = {
                    if (regName.isBlank() || regEmail.isBlank() || regPassword.isBlank()) {
                      errorMessage = "Please fill in all required fields."
                      return@Button
                    }
                    if (regPassword != regConfirmPassword) {
                      errorMessage = "Passwords do not match."
                      return@Button
                    }
                    if (!isStudentRegEmailVerified) {
                      errorMessage = "Please verify your student email with the 6-digit verification code sent via Brevo before creating an account."
                      return@Button
                    }
                    isLoading = true
                    coroutineScope.launch {
                      val res = authRepository.registerStudent(
                        name = regName,
                        email = regEmail,
                        college = regCollege,
                        branch = regBranch,
                        semester = regSemester
                      )
                      isLoading = false
                      res.onSuccess {
                        onShowToast("Student account created! Welcome, ${it.name}.")
                        onAuthSuccess()
                      }.onFailure {
                        errorMessage = it.message ?: "Registration failed."
                      }
                    }
                  },
                  enabled = !isLoading,
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = theme.accentCyan,
                    contentColor = if (theme.isDark) Color(0xFF080A18) else Color.White
                  ),
                  modifier = Modifier.fillMaxWidth().height(48.dp).testTag("reg_submit_btn")
                ) {
                  if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                  } else {
                    Text("CREATE STUDENT ACCOUNT", fontWeight = FontWeight.Bold)
                  }
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Already registered?", color = theme.textSecondary, fontSize = 12.sp)
                  TextButton(
                    onClick = {
                      errorMessage = null
                      currentStep = AuthStep.STUDENT_LOGIN
                    }
                  ) {
                    Text("SIGN IN", color = theme.accentCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  }
                }
              }
            }

            AuthStep.TEACHER_LOGIN -> {
              // Section 7: Teacher Login Only (NO CREATE TEACHER ACCOUNT OPTION)
              Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  IconButton(
                    onClick = {
                      errorMessage = null
                      currentStep = AuthStep.ROLE_SELECT
                    }
                  ) {
                    Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                      contentDescription = "Back",
                      tint = theme.accentViolet
                    )
                  }
                  Text(
                    text = "Teacher Login",
                    color = theme.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = theme.fontFamily
                  )
                }

                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.accentViolet.copy(alpha = 0.12f))
                    .border(1.dp, theme.accentViolet.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
                ) {
                  Text(
                    text = "Teacher accounts are provisioned by college administration.",
                    color = theme.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                  )
                }

                OutlinedTextField(
                  value = teacherIdentifier,
                  onValueChange = { teacherIdentifier = it },
                  label = { Text("Teacher Email / Username") },
                  placeholder = { Text("e.g. teacher@neonotes.edu") },
                  leadingIcon = { Icon(Icons.Default.SupervisorAccount, contentDescription = null, tint = theme.accentViolet) },
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.accentViolet,
                    unfocusedBorderColor = theme.glassBorder,
                    focusedTextColor = theme.textPrimary,
                    unfocusedTextColor = theme.textPrimary
                  ),
                  modifier = Modifier.fillMaxWidth().testTag("teacher_identifier_input")
                )

                OutlinedTextField(
                  value = teacherPassword,
                  onValueChange = { teacherPassword = it },
                  label = { Text("Password") },
                  leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = theme.accentViolet) },
                  trailingIcon = {
                    IconButton(onClick = { teacherPasswordVisible = !teacherPasswordVisible }) {
                      Icon(
                        imageVector = if (teacherPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = theme.textSecondary
                      )
                    }
                  },
                  visualTransformation = if (teacherPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.accentViolet,
                    unfocusedBorderColor = theme.glassBorder,
                    focusedTextColor = theme.textPrimary,
                    unfocusedTextColor = theme.textPrimary
                  ),
                  modifier = Modifier.fillMaxWidth().testTag("teacher_password_input")
                )

                Button(
                  onClick = {
                    if (teacherIdentifier.isBlank() || teacherPassword.isBlank()) {
                      errorMessage = "Please enter your teacher credentials."
                      return@Button
                    }
                    isLoading = true
                    coroutineScope.launch {
                      val res = authRepository.loginTeacher(teacherIdentifier, teacherPassword)
                      isLoading = false
                      res.onSuccess {
                        onShowToast("Welcome, Professor ${it.name}!")
                        onAuthSuccess()
                      }.onFailure {
                        errorMessage = it.message ?: "Teacher authentication failed."
                      }
                    }
                  },
                  enabled = !isLoading,
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = theme.accentViolet,
                    contentColor = Color.White
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("teacher_login_submit_btn")
                ) {
                  if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                  } else {
                    Text("LOGIN AS TEACHER", fontWeight = FontWeight.Bold)
                  }
                }

                // Section 7: "Do NOT show: Create Teacher Account" - intentionally omitted
              }
            }
          }
        }
      }
    }
  }
}
