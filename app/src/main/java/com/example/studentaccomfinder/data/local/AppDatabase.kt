package com.example.studentaccomfinder.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studentaccomfinder.data.local.dao.AccommodationDao
import com.example.studentaccomfinder.data.local.dao.ChatMessageDao
import com.example.studentaccomfinder.data.local.dao.UserDao
import com.example.studentaccomfinder.data.local.entity.Accommodation
import com.example.studentaccomfinder.data.local.entity.ChatMessage
import com.example.studentaccomfinder.data.local.entity.User
import com.example.studentaccomfinder.utils.Constants

@Database(
    entities = [
        User::class,
        Accommodation::class,
        ChatMessage::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun accommodationDao(): AccommodationDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}