package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ActivityItem
import com.example.data.model.Bookmark
import com.example.data.model.Note
import com.example.data.model.NotificationItem
import com.example.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.example.data.model.EmailVerification
import kotlinx.coroutines.launch

@Database(
  entities = [
    Note::class,
    User::class,
    Bookmark::class,
    NotificationItem::class,
    ActivityItem::class,
    EmailVerification::class
  ],
  version = 4,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun noteDao(): NoteDao
  abstract fun userDao(): UserDao
  abstract fun bookmarkDao(): BookmarkDao
  abstract fun notificationDao(): NotificationDao
  abstract fun activityDao(): ActivityDao
  abstract fun emailVerificationDao(): EmailVerificationDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "neonotes_database"
        )
        .fallbackToDestructiveMigration()
        .addCallback(DatabaseCallback(scope))
        .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateDatabase(database)
          }
        }
      }
    }

    private suspend fun populateDatabase(database: AppDatabase) {
      val userDao = database.userDao()
      val noteDao = database.noteDao()
      val notificationDao = database.notificationDao()

      userDao.insertUsers(SeedData.initialUsers)
      noteDao.insertNotes(SeedData.initialNotes)
      notificationDao.insertNotifications(SeedData.initialNotifications)
    }
  }
}
