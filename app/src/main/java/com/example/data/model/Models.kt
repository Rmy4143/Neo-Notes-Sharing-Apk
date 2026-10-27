package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
  @PrimaryKey val id: String,
  val name: String,
  val email: String,
  val college: String,
  val branch: String,
  val semester: String,
  val role: String = "STUDENT", // "STUDENT" or "TEACHER" / "ADMIN"
  val avatarUrl: String = "",
  val isEmailVerified: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)

/**
 * Section 6: Note Record with NitroFlare Cloud Storage reference and metadata.
 * Stores document reference and xxhash without storing raw document binary in MySQL/Room.
 */
@Entity(tableName = "notes")
data class Note(
  @PrimaryKey val id: String,
  val userId: String,
  val uploaderName: String,
  val title: String,
  val description: String,
  val fileName: String,
  val fileUrl: String, // NitroFlare direct view URL
  val nitroflareUrl: String? = null,
  val nitroflareXxhash: String? = null,
  val fileType: String, // PDF, DOC, DOCX, PPT, PPTX
  val fileSize: String,
  val fileSizeBytes: Long = 0L,
  val subject: String,
  val branch: String,
  val semester: String,
  val university: String,
  val academicYear: String,
  val tags: String, // Comma separated tags e.g. "DSA, Algorithms, Trees, Exam"
  val status: String = "PENDING", // PENDING, APPROVED, REJECTED
  val rejectionReason: String? = null,
  val downloadCount: Int = 0,
  val bookmarkCount: Int = 0,
  val lastAccessedAt: Long? = null,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
) {
  val effectiveUrl: String
    get() = nitroflareUrl?.ifBlank { null } ?: fileUrl

  // Flag if note is inactive on NitroFlare (> 60 days without download or access)
  val isInactiveOnNitroFlare: Boolean
    get() = (System.currentTimeMillis() - (lastAccessedAt ?: createdAt)) > (60L * 24 * 3600 * 1000)
}

@Entity(tableName = "bookmarks")
data class Bookmark(
  @PrimaryKey val id: String,
  val noteId: String,
  val userId: String,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationItem(
  @PrimaryKey val id: String,
  val userId: String,
  val title: String,
  val message: String,
  val type: String, // APPROVAL, REJECTION, MILESTONE, SYSTEM
  val isRead: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "activity_logs")
data class ActivityItem(
  @PrimaryKey val id: String,
  val userId: String,
  val title: String,
  val timestamp: Long = System.currentTimeMillis(),
  val type: String = "NOTE"
)

data class DashboardStats(
  val totalUploaded: Int = 0,
  val totalApproved: Int = 0,
  val totalPending: Int = 0,
  val totalDownloads: Int = 0,
  val totalBookmarks: Int = 0
)

data class AdminStats(
  val totalUsers: Int = 0,
  val totalNotes: Int = 0,
  val pendingApprovals: Int = 0,
  val approvedNotes: Int = 0,
  val rejectedNotes: Int = 0,
  val totalDownloads: Int = 0,
  val totalStorageBytes: Long = 0L,
  val totalStorageFormatted: String = "0 MB",
  val inactiveNotesCount: Int = 0
)

/**
 * Section 5: Real Server/Client Verified Email OTP Record.
 * Matches email_verifications table schema.
 * Stores HMAC/SHA-256 hash of OTP with expiration and attempt limiting.
 */
@Entity(tableName = "email_verifications")
data class EmailVerification(
  @PrimaryKey val id: String,
  val email: String,
  val otpHash: String,
  val purpose: String = "STUDENT_EMAIL_VERIFICATION",
  val expiresAt: Long,
  val attemptCount: Int = 0,
  val verified: Boolean = false,
  val lastSentAt: Long = System.currentTimeMillis(),
  val createdAt: Long = System.currentTimeMillis()
)

