package com.example.data.remote

/**
 * Section 3: Centralized API configuration for NEO NOTES.
 * Centralizes all backend network endpoints without scattering hardcoded URLs across Kotlin files.
 *
 * - For Android Emulator development: http://10.0.2.2:5000
 * - For Physical Android device: http://<DEVELOPMENT_PC_LAN_IP>:5000
 * - For Deployed Production backend: Deployed HTTPS endpoint
 */
object ApiConfig {

  /**
   * Default Base URL for Android Emulator communicating with local host backend service.
   */
  const val EMULATOR_BASE_URL = "http://10.0.2.2:5000"

  /**
   * Central Base URL used across the application.
   */
  @Volatile
  var BASE_URL: String = EMULATOR_BASE_URL
    private set

  /**
   * Normalizes the base URL ensuring no trailing slash.
   */
  fun setBaseUrl(url: String) {
    val clean = url.trim().removeSuffix("/")
    if (clean.isNotEmpty()) {
      BASE_URL = clean
    }
  }

  // Authentication & OTP Endpoints (Section 4, 5)
  val SEND_STUDENT_OTP_URL: String get() = "$BASE_URL/api/auth/student/send-otp"
  val VERIFY_STUDENT_OTP_URL: String get() = "$BASE_URL/api/auth/student/verify-otp"
  val VERIFICATION_STATUS_URL: String get() = "$BASE_URL/api/auth/student/verification-status"

  // Service Health & Storage Endpoints
  val HEALTH_URL: String get() = "$BASE_URL/api/health"
  val UPLOAD_URL: String get() = "$BASE_URL/api/upload"
  val STATS_URL: String get() = "$BASE_URL/api/stats"
}
