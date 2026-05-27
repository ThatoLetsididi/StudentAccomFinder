package com.example.studentaccomfinder.data.local

import androidx.room.TypeConverter
import java.util.Date

/**
 * TypeConverters — teach Room how to store complex types
 *
 * Room only knows basic types: Int, Long, String, Double, etc.
 * We need converters for: Date, List, custom enums, etc.
 *
 * For now, we convert Date to Long (timestamp)
 */
class Converters {

    /**
     * Convert Long (timestamp) to Date
     * Room calls this when READING from database
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Convert Date to Long (timestamp)
     * Room calls this when WRITING to database
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}