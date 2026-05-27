package com.example.studentaccomfinder.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "accommodations",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["providerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["providerId"])]
)
data class Accommodation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val providerId: Long,
    val title: String,
    val description: String,
    val price: Double,
    val location: String,
    val type: String,
    val amenities: String,
    val availabilityDate: Long,
    val depositAmount: Double,
    val imageName: String?,
    val latitude: Double,
    val longitude: Double,
    val status: String = "AVAILABLE",
    val reservedByStudentId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)