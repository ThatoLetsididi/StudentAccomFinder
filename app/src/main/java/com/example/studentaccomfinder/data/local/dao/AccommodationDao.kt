package com.example.studentaccomfinder.data.local.dao

import androidx.room.*
import com.example.studentaccomfinder.data.local.entity.Accommodation
import kotlinx.coroutines.flow.Flow

/**
 * AccommodationDao — CRUD operations for accommodation listings
 */
@Dao
interface AccommodationDao {

    /** Create new listing */
    @Insert
    suspend fun insertAccommodation(accommodation: Accommodation): Long

    /** Update existing listing */
    @Update
    suspend fun updateAccommodation(accommodation: Accommodation)

    /** Delete listing */
    @Delete
    suspend fun deleteAccommodation(accommodation: Accommodation)

    /** Get all listings */
    @Query("SELECT * FROM accommodations ORDER BY createdAt DESC")
    fun getAllAccommodations(): Flow<List<Accommodation>>

    /** Get listings by provider */
    @Query("SELECT * FROM accommodations WHERE providerId = :providerId ORDER BY createdAt DESC")
    fun getAccommodationsByProvider(providerId: Long): Flow<List<Accommodation>>

    /** Get single listing by ID */
    @Query("SELECT * FROM accommodations WHERE id = :id LIMIT 1")
    suspend fun getAccommodationById(id: Long): Accommodation?

    /** Get available listings only */
    @Query("SELECT * FROM accommodations WHERE status = 'AVAILABLE' ORDER BY price ASC")
    fun getAvailableAccommodations(): Flow<List<Accommodation>>

    /** Filter by price range */
    @Query("SELECT * FROM accommodations WHERE price BETWEEN :minPrice AND :maxPrice AND status = 'AVAILABLE'")
    fun filterByPrice(minPrice: Double, maxPrice: Double): Flow<List<Accommodation>>

    /** Filter by location */
    @Query("SELECT * FROM accommodations WHERE location LIKE '%' || :location || '%' AND status = 'AVAILABLE'")
    fun filterByLocation(location: String): Flow<List<Accommodation>>

    /** Filter by availability date */
    @Query("SELECT * FROM accommodations WHERE availabilityDate <= :date AND status = 'AVAILABLE'")
    fun filterByAvailabilityDate(date: Long): Flow<List<Accommodation>>

    /**
     * Combined filter: price + location + date
     */
    @Query("SELECT * FROM accommodations WHERE price BETWEEN :minPrice AND :maxPrice AND location LIKE '%' || :location || '%' AND availabilityDate <= :date AND status = 'AVAILABLE' ORDER BY price ASC")
    fun filterCombined(minPrice: Double, maxPrice: Double, location: String, date: Long): Flow<List<Accommodation>>

    /** Reserve listing (update status and student) */
    @Query("UPDATE accommodations SET status = 'RESERVED', reservedByStudentId = :studentId WHERE id = :accommodationId")
    suspend fun reserveAccommodation(accommodationId: Long, studentId: Long)

    /** Get count of listings by provider */
    @Query("SELECT COUNT(*) FROM accommodations WHERE providerId = :providerId")
    suspend fun getListingCount(providerId: Long): Int


}