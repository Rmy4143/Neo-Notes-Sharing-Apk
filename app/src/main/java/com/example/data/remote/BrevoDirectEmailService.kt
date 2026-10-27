package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.concurrent.TimeUnit

/**
 * Direct Brevo REST API v3 Integration Service.
 * Provides resilient, direct transactional email delivery from Android over HTTPS.
 * Eliminates emulator connection failures (e.g. Failed to connect to /10.0.2.2:5000)
 * while ensuring REAL 6-digit cryptographically generated OTPs delivered through Brevo.
 */
object BrevoDirectEmailService {

  private const val BREVO_API_URL = "https://api.brevo.com/v3/smtp/email"
  private const val BREVO_API_KEY = ""
  private const val SENDER_EMAIL = "alitaazib1@gmail.com"
  private const val SENDER_NAME = "NEO NOTES Security"

  const val OTP_SECRET = "neonotes_secure_otp_salt_secret_2026"
  const val OTP_EXPIRY_MS = 10L * 60L * 1000L // 10 minutes
  const val RESEND_COOLDOWN_MS = 60L * 1000L // 60 seconds
  const val MAX_ATTEMPTS = 5

  private val httpClient = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(25, TimeUnit.SECONDS)
    .build()

  private val secureRandom = SecureRandom()

  /**
   * Generates a cryptographically secure 6-digit OTP code (100000 - 999999).
   */
  fun generateSecureSixDigitOtp(): String {
    val number = 100000 + secureRandom.nextInt(900000)
    return number.toString()
  }

  /**
   * Computes SHA-256 hash of email + otp + salt secret.
   * Plaintext OTP is NEVER stored.
   */
  fun hashOtp(email: String, otp: String): String {
    val cleanEmail = email.trim().lowercase()
    val cleanOtp = otp.trim()
    val input = "$cleanEmail:$cleanOtp:$OTP_SECRET"
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(input.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { "%02x".format(it) }
  }

  /**
   * Dispatches the 6-digit OTP via Brevo Transactional Email REST API.
   * Sends real email to the user's inbox from alitaazib1@gmail.com.
   */
  suspend fun sendOtpEmail(toEmail: String, otpCode: String): Result<String> = withContext(Dispatchers.IO) {
    val cleanEmail = toEmail.trim().lowercase()
    if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
      return@withContext Result.failure(Exception("Please provide a valid student email address."))
    }

    try {
      val payload = JSONObject().apply {
        put("sender", JSONObject().apply {
          put("name", SENDER_NAME)
          put("email", SENDER_EMAIL)
        })
        put("to", JSONArray().apply {
          put(JSONObject().apply {
            put("email", cleanEmail)
            put("name", "NEO Student")
          })
        })
        put("subject", "NEO NOTES // Verification Code: $otpCode")
        put("htmlContent", buildCyberEmailHtml(cleanEmail, otpCode))
      }

      val requestBody = payload.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
      val request = Request.Builder()
        .url(BREVO_API_URL)
        .addHeader("api-key", BREVO_API_KEY)
        .addHeader("accept", "application/json")
        .addHeader("content-type", "application/json")
        .post(requestBody)
        .build()

      val response = httpClient.newCall(request).execute()
      val responseText = response.body?.string().orEmpty()

      if (response.isSuccessful) {
        val json = runCatching { JSONObject(responseText) }.getOrNull()
        val messageId = json?.optString("messageId") ?: "delivered"
        Result.success("Verification code sent to your email (MessageId: $messageId)")
      } else {
        val json = runCatching { JSONObject(responseText) }.getOrNull()
        val errorMsg = json?.optString("message") ?: "Brevo returned HTTP ${response.code}"
        Result.failure(Exception("Unable to send verification code: $errorMsg"))
      }
    } catch (e: Exception) {
      Result.failure(Exception("Brevo network dispatch error: ${e.message ?: "Connection failed"}"))
    }
  }

  /**
   * Builds the cyber/neon branded HTML email template for Brevo delivery.
   */
  private fun buildCyberEmailHtml(recipientEmail: String, otpCode: String): String {
    return """
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>NEO NOTES Verification</title>
        <style>
          body { margin: 0; padding: 0; background-color: #080A18; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #E0E7FF; }
          .container { max-width: 560px; margin: 40px auto; background: #0E1228; border: 1px solid #1E2958; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 40px rgba(0, 240, 255, 0.15); }
          .header { background: linear-gradient(135deg, #0E1228 0%, #151B38 100%); padding: 32px 24px; text-align: center; border-bottom: 1px solid #1E2958; }
          .brand-title { font-size: 26px; font-weight: 800; color: #00F0FF; letter-spacing: 2px; margin: 0; text-transform: uppercase; }
          .brand-subtitle { font-size: 13px; color: #7B8BA6; margin-top: 6px; letter-spacing: 1px; }
          .content { padding: 32px 28px; text-align: center; }
          .greeting { font-size: 18px; font-weight: 600; color: #FFFFFF; margin-bottom: 12px; }
          .description { font-size: 14px; color: #94A3B8; line-height: 1.6; margin-bottom: 28px; }
          .otp-box { background: rgba(0, 240, 255, 0.06); border: 2px dashed #00F0FF; border-radius: 12px; padding: 20px 10px; margin: 20px 0; display: inline-block; width: 85%; }
          .otp-code { font-family: 'Courier New', Courier, monospace; font-size: 38px; font-weight: 900; color: #00F0FF; letter-spacing: 8px; margin: 0; }
          .meta-info { font-size: 12px; color: #64748B; margin-top: 20px; line-height: 1.5; }
          .footer { background: #0A0D1F; padding: 20px; text-align: center; border-top: 1px solid #1E2958; font-size: 11px; color: #475569; }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="header">
            <h1 class="brand-title">NEO NOTES</h1>
            <div class="brand-subtitle">CAMPUS REPOSITORY // IDENTITY VERIFICATION</div>
          </div>
          <div class="content">
            <div class="greeting">Student Verification Code</div>
            <div class="description">
              You are completing student identity verification for <strong>$recipientEmail</strong>.<br/>
              Use the single-use 6-digit verification code below to authenticate your account.
            </div>
            <div class="otp-box">
              <div class="otp-code">$otpCode</div>
            </div>
            <div class="meta-info">
              ⏱ This code expires in <strong>10 minutes</strong>.<br/>
              🔒 Never share this code with anyone. NEO NOTES staff will never ask for your verification code.
            </div>
          </div>
          <div class="footer">
            &copy; 2026 NEO NOTES Campus Repository. All rights reserved.<br/>
            Automated security dispatch via Brevo Transactional Mailer.
          </div>
        </div>
      </body>
      </html>
    """.trimIndent()
  }
}
