package com.example.data.storage

import android.net.Uri

data class StorageUploadResult(
  val isSuccess: Boolean,
  val fileUrl: String,
  val fileName: String,
  val fileSizeBytes: Long,
  val fileXxhash: String? = null,
  val providerName: String,
  val status: String = "PENDING",
  val errorMessage: String? = null
)

data class StorageHealthStatus(
  val isConnected: Boolean,
  val providerName: String,
  val activeServer: String? = null,
  val message: String
)

data class StorageMetrics(
  val totalNotes: Int,
  val totalFiles: Int,
  val totalStorageBytes: Long,
  val formattedStorage: String,
  val inactiveNotesCount: Int,
  val inactiveNotes: List<InactiveNoteInfo> = emptyList(),
  val recentlyAccessed: List<RecentAccessInfo> = emptyList()
)

data class InactiveNoteInfo(
  val id: String,
  val title: String,
  val fileSize: String,
  val lastAccessedAt: Long,
  val daysInactive: Long
)

data class RecentAccessInfo(
  val id: String,
  val title: String,
  val downloadCount: Int,
  val lastAccessedAt: Long
)

/**
 * Section 17: Storage Abstraction Layer
 * The rest of the application depends on this interface rather than
 * directly on vendor-specific storage details.
 */
interface FileStorageProvider {
  val providerName: String

  suspend fun uploadFile(
    uri: Uri,
    fileName: String,
    mimeType: String,
    onProgress: (Float) -> Unit
  ): StorageUploadResult

  suspend fun testConnection(): Result<StorageHealthStatus>

  fun openInBrowser(url: String)

  fun downloadToDevice(url: String, fileName: String): Boolean

  fun copyLinkToClipboard(url: String)
}
