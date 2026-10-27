package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ActivityItem
import com.example.data.model.Bookmark
import com.example.data.model.Note
import com.example.data.model.NotificationItem
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
  @Query("SELECT * FROM notes ORDER BY createdAt DESC")
  fun getAllNotes(): Flow<List<Note>>

  @Query("SELECT * FROM notes WHERE status = 'APPROVED' ORDER BY createdAt DESC")
  fun getApprovedNotes(): Flow<List<Note>>

  @Query("SELECT * FROM notes WHERE status = 'PENDING' ORDER BY createdAt DESC")
  fun getPendingNotes(): Flow<List<Note>>

  @Query("SELECT * FROM notes WHERE userId = :userId ORDER BY createdAt DESC")
  fun getNotesByUser(userId: String): Flow<List<Note>>

  @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
  fun getNoteById(id: String): Flow<Note?>

  @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
  suspend fun getNoteByIdSync(id: String): Note?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNote(note: Note)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotes(notes: List<Note>)

  @Update
  suspend fun updateNote(note: Note)

  @Query("DELETE FROM notes WHERE id = :id")
  suspend fun deleteNoteById(id: String)

  @Query("UPDATE notes SET downloadCount = downloadCount + 1, lastAccessedAt = :time, updatedAt = :time WHERE id = :id")
  suspend fun incrementDownloadCountWithTime(id: String, time: Long = System.currentTimeMillis())

  @Query("UPDATE notes SET downloadCount = downloadCount + 1, lastAccessedAt = :time WHERE id = :id")
  suspend fun incrementDownloadCount(id: String, time: Long = System.currentTimeMillis())

  @Query("UPDATE notes SET status = :status, rejectionReason = :reason, updatedAt = :time WHERE id = :id")
  suspend fun updateStatus(id: String, status: String, reason: String? = null, time: Long = System.currentTimeMillis())

  @Query("UPDATE notes SET bookmarkCount = bookmarkCount + :delta WHERE id = :id")
  suspend fun updateBookmarkCount(id: String, delta: Int)

  @Query("SELECT * FROM notes WHERE (lastAccessedAt IS NULL AND createdAt < :cutoffTime) OR (lastAccessedAt < :cutoffTime)")
  fun getInactiveNotes(cutoffTime: Long): Flow<List<Note>>
}

@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  fun getUserById(id: String): Flow<User?>

  @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
  suspend fun getUserByEmail(email: String): User?

  @Query("SELECT * FROM users ORDER BY createdAt DESC")
  fun getAllUsers(): Flow<List<User>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: User)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUsers(users: List<User>)

  @Update
  suspend fun updateUser(user: User)

  @Query("UPDATE users SET isEmailVerified = :verified WHERE LOWER(email) = LOWER(:email)")
  suspend fun updateEmailVerified(email: String, verified: Boolean)
}

@Dao
interface BookmarkDao {
  @Query("SELECT * FROM bookmarks WHERE userId = :userId ORDER BY createdAt DESC")
  fun getBookmarksByUser(userId: String): Flow<List<Bookmark>>

  @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE noteId = :noteId AND userId = :userId)")
  fun isBookmarked(noteId: String, userId: String): Flow<Boolean>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmark(bookmark: Bookmark)

  @Query("DELETE FROM bookmarks WHERE noteId = :noteId AND userId = :userId")
  suspend fun deleteBookmark(noteId: String, userId: String)
}

@Dao
interface NotificationDao {
  @Query("SELECT * FROM notifications WHERE userId = :userId OR userId = 'ALL' ORDER BY createdAt DESC")
  fun getNotificationsByUser(userId: String): Flow<List<NotificationItem>>

  @Query("SELECT COUNT(*) FROM notifications WHERE (userId = :userId OR userId = 'ALL') AND isRead = 0")
  fun getUnreadCount(userId: String): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: NotificationItem)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotifications(notifications: List<NotificationItem>)

  @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
  suspend fun markAsRead(id: String)

  @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId OR userId = 'ALL'")
  suspend fun markAllAsRead(userId: String)
}

@Dao
interface ActivityDao {
  @Query("SELECT * FROM activity_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT 20")
  fun getRecentActivities(userId: String): Flow<List<ActivityItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertActivity(activity: ActivityItem)
}

@Dao
interface EmailVerificationDao {
  @Query("SELECT * FROM email_verifications WHERE LOWER(email) = LOWER(:email) ORDER BY createdAt DESC LIMIT 1")
  suspend fun getLatestVerification(email: String): com.example.data.model.EmailVerification?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertVerification(verification: com.example.data.model.EmailVerification)

  @Query("UPDATE email_verifications SET attemptCount = :attemptCount WHERE id = :id")
  suspend fun updateAttemptCount(id: String, attemptCount: Int)

  @Query("UPDATE email_verifications SET verified = :verified WHERE id = :id")
  suspend fun updateVerified(id: String, verified: Boolean)

  @Query("DELETE FROM email_verifications WHERE LOWER(email) = LOWER(:email)")
  suspend fun deleteForEmail(email: String)
}

