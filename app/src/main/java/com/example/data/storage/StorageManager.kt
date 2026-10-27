package com.example.data.storage

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * High-level Storage Manager providing seamless abstraction between
 * NitroFlare Cloud Storage and Local Fallback Storage.
 */
class StorageManager(private val context: Context) {
  val fallbackProvider = LocalFallbackStorageProvider(context)
  val nitroFlareProvider = NitroFlareStorageProvider(context, fallbackProvider)

  // Active provider defaults to NitroFlare
  val activeProvider: FileStorageProvider
    get() = nitroFlareProvider

  suspend fun uploadFile(
    uri: Uri,
    fileName: String,
    mimeType: String,
    onProgress: (Float) -> Unit
  ): StorageUploadResult {
    return activeProvider.uploadFile(uri, fileName, mimeType, onProgress)
  }

  suspend fun testConnection(): Result<StorageHealthStatus> {
    return activeProvider.testConnection()
  }

  fun openInBrowser(url: String) {
    activeProvider.openInBrowser(url)
  }

  fun downloadToDevice(url: String, fileName: String): Boolean {
    return activeProvider.downloadToDevice(url, fileName)
  }

  fun copyLinkToClipboard(url: String) {
    activeProvider.copyLinkToClipboard(url)
  }
}
