package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AuthRepository
import com.example.ui.theme.LocalNeoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Section 1, 2, 3, 4, 8, 18, 19:
 * Real Server-side Email OTP Verification Component using Brevo.
 * - OTP field appears with smooth animation only AFTER backend & Brevo confirm dispatch.
 * - Proper 6-digit numeric input with paste & backspace support.
 * - 60-second resend cooldown timer.
 * - Never fakes verification, never uses hardcoded OTP.
 */
@Composable
fun OtpVerificationComponent(
  email: String,
  isVerified: Boolean,
  onVerified: () -> Unit,
  authRepository: AuthRepository,
  coroutineScope: CoroutineScope,
  onShowToast: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val theme = LocalNeoTheme.current

  // State
  var isSendingOtp by remember { mutableStateOf(false) }
  var isOtpSent by remember { mutableStateOf(false) }
  var isVerifying by remember { mutableStateOf(false) }
  var otpInput by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var statusMessage by remember { mutableStateOf<String?>(null) }
  var cooldownSeconds by remember { mutableIntStateOf(0) }

  val focusRequester = remember { FocusRequester() }

  // 60-second cooldown timer effect
  LaunchedEffect(cooldownSeconds) {
    if (cooldownSeconds > 0) {
      delay(1000L)
      cooldownSeconds -= 1
    }
  }

  // Auto-verify when 6 digits are fully entered
  LaunchedEffect(otpInput) {
    if (otpInput.length == 6 && !isVerified && !isVerifying) {
      isVerifying = true
      errorMessage = null
      coroutineScope.launch {
        val result = authRepository.verifyStudentOtp(email, otpInput)
        isVerifying = false
        result.onSuccess {
          statusMessage = "✓ Email verified successfully"
          errorMessage = null
          onVerified()
          onShowToast("✓ Email verified successfully")
        }.onFailure {
          errorMessage = it.message ?: "Invalid verification code."
        }
      }
    }
  }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // 1. If already verified, show green verified badge
    if (isVerified) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0x2200E676))
          .border(1.dp, Color(0xFF00E676), RoundedCornerShape(10.dp))
          .padding(horizontal = 12.dp, vertical = 10.dp)
          .testTag("otp_verified_badge"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "Verified",
          tint = Color(0xFF00E676),
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "✓ Student Email Verified",
          color = Color(0xFF00E676),
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }
    } else {
      // 2. Button directly below Email: [ SEND VERIFICATION CODE ]
      if (!isOtpSent) {
        OutlinedButton(
          onClick = {
            val cleanEmail = email.trim()
            if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
              errorMessage = "Please enter a valid student email address first."
              return@OutlinedButton
            }
            errorMessage = null
            statusMessage = "Sending verification code..."
            isSendingOtp = true

            coroutineScope.launch {
              val result = authRepository.sendStudentOtp(cleanEmail)
              isSendingOtp = false
              result.onSuccess { msg ->
                isOtpSent = true
                cooldownSeconds = 60
                statusMessage = "✓ Verification code sent"
                errorMessage = null
                onShowToast("6-digit verification code dispatched via Brevo to $cleanEmail")
              }.onFailure {
                statusMessage = null
                errorMessage = it.message ?: "Failed to send verification code."
              }
            }
          },
          enabled = !isSendingOtp && email.isNotBlank(),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = theme.accentCyan
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("send_otp_btn")
        ) {
          if (isSendingOtp) {
            CircularProgressIndicator(
              modifier = Modifier.size(16.dp),
              color = theme.accentCyan,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sending verification code...", fontSize = 12.sp)
          } else {
            Icon(
              imageVector = Icons.Default.MarkEmailRead,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "SEND VERIFICATION CODE",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 0.5.sp
            )
          }
        }
      }

      // 3. Status or Error messages before OTP section
      if (statusMessage != null && !isOtpSent) {
        Text(
          text = statusMessage!!,
          color = theme.accentCyan,
          fontSize = 12.sp,
          modifier = Modifier.padding(horizontal = 4.dp)
        )
      }

      if (errorMessage != null && !isOtpSent) {
        Text(
          text = errorMessage!!,
          color = Color(0xFFFF5252),
          fontSize = 12.sp,
          modifier = Modifier.padding(horizontal = 4.dp)
        )
      }

      // 4. Section 3: Smooth Animated OTP Input section appears AFTER OTP is sent
      AnimatedVisibility(
        visible = isOtpSent,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        GlassCard(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          shape = RoundedCornerShape(16.dp),
          borderGlow = true,
          glowColor = theme.accentCyan,
          contentPadding = 14.dp
        ) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Header
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = theme.accentCyan,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "VERIFICATION CODE",
                color = theme.accentCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            }

            Text(
              text = "A 6-digit code has been dispatched via Brevo to:\n$email",
              color = theme.textSecondary,
              fontSize = 12.sp,
              textAlign = TextAlign.Center,
              lineHeight = 16.sp
            )

            // Section 19: 6-Digit OTP Box Grid with Paste & Single Input Handling
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { focusRequester.requestFocus() },
              contentAlignment = Alignment.Center
            ) {
              // Hidden actual TextField to capture input, paste, and backspace
              BasicTextField(
                value = otpInput,
                onValueChange = { input ->
                  // Allow only numeric digits, max 6 characters
                  val filtered = input.filter { it.isDigit() }.take(6)
                  otpInput = filtered
                },
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.NumberPassword
                ),
                keyboardActions = KeyboardActions(
                  onDone = {
                    if (otpInput.length == 6 && !isVerifying) {
                      isVerifying = true
                      coroutineScope.launch {
                        val result = authRepository.verifyStudentOtp(email, otpInput)
                        isVerifying = false
                        result.onSuccess {
                          statusMessage = "✓ Email verified successfully"
                          errorMessage = null
                          onVerified()
                        }.onFailure {
                          errorMessage = it.message ?: "Invalid code."
                        }
                      }
                    }
                  }
                ),
                modifier = Modifier
                  .size(1.dp)
                  .focusRequester(focusRequester)
                  .testTag("otp_hidden_input")
              )

              // Visual 6-Box Digits Display
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                for (i in 0 until 6) {
                  val digit = otpInput.getOrNull(i)?.toString() ?: ""
                  val isCurrent = i == otpInput.length
                  val borderColor = when {
                    errorMessage != null -> Color(0xFFFF5252)
                    isCurrent -> theme.accentCyan
                    digit.isNotEmpty() -> theme.accentCyan.copy(alpha = 0.7f)
                    else -> theme.glassBorder
                  }

                  Box(
                    modifier = Modifier
                      .size(width = 42.dp, height = 50.dp)
                      .clip(RoundedCornerShape(10.dp))
                      .background(theme.bgNavy.copy(alpha = 0.8f))
                      .border(
                        width = if (isCurrent) 1.8.dp else 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(10.dp)
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = digit,
                      color = theme.textPrimary,
                      fontSize = 22.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = FontFamily.Monospace,
                      textAlign = TextAlign.Center
                    )
                  }
                }
              }
            }

            // Error Message Banner inside OTP Section
            errorMessage?.let { err ->
              Text(
                text = "✕ $err",
                color = Color(0xFFFF5252),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
              )
            }

            // [ VERIFY CODE ] Button
            Button(
              onClick = {
                if (otpInput.length != 6) {
                  errorMessage = "Please enter all 6 digits."
                  return@Button
                }
                isVerifying = true
                errorMessage = null

                coroutineScope.launch {
                  val result = authRepository.verifyStudentOtp(email, otpInput)
                  isVerifying = false
                  result.onSuccess {
                    statusMessage = "✓ Email verified successfully"
                    errorMessage = null
                    onVerified()
                    onShowToast("✓ Email verified successfully")
                  }.onFailure {
                    errorMessage = it.message ?: "Invalid verification code."
                  }
                }
              },
              enabled = otpInput.length == 6 && !isVerifying,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = theme.accentCyan,
                contentColor = if (theme.isDark) Color(0xFF080A18) else Color.White
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("verify_code_btn")
            ) {
              if (isVerifying) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = Color.Black,
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Verifying...", fontWeight = FontWeight.Bold)
              } else {
                Text("VERIFY CODE", fontWeight = FontWeight.Bold)
              }
            }

            // Section 8: Resend Code with 60-Second Cooldown
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (cooldownSeconds > 0) {
                Text(
                  text = "Resend code in ${cooldownSeconds}s",
                  color = theme.textSecondary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium
                )
              } else {
                OutlinedButton(
                  onClick = {
                    isSendingOtp = true
                    errorMessage = null
                    coroutineScope.launch {
                      val result = authRepository.sendStudentOtp(email)
                      isSendingOtp = false
                      result.onSuccess {
                        cooldownSeconds = 60
                        otpInput = ""
                        onShowToast("New verification code dispatched via Brevo")
                      }.onFailure {
                        errorMessage = it.message ?: "Failed to resend code."
                      }
                    }
                  },
                  enabled = !isSendingOtp,
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.height(34.dp).testTag("resend_otp_btn")
                ) {
                  Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = theme.accentCyan
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "RESEND CODE",
                    color = theme.accentCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
