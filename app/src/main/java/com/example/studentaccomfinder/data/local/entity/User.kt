package com.example.studentaccomfinder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val role: String,
    val studentId: String? = null,
    val companyName: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)