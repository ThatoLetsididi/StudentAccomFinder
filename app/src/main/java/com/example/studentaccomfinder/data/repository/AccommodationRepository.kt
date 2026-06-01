package com.example.studentaccomfinder.data.repository

import com.example.studentaccomfinder.data.local.AppDatabase
import com.example.studentaccomfinder.data.local.entity.Accommodation
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * AccommodationRepository — single source of truth for listings
 */
class AccommodationRepository(database: AppDatabase) {

    private val accommodationDao = database.accommodationDao()

    suspend fun addListing(accommodation: Accommodation): Result<Long> {
        return try {
            // Validation
            if (accommodation.title.isBlank()) {
                return Result.failure(Exception("Title is required"))
            }
            if (accommodation.price <= 0) {
                return Result.failure(Exception("Price must be greater than 0"))
            }
            if (accommodation.location.isBlank()) {
                return Result.failure(Exception("Location is required"))
            }

            val id = accommodationDao.insertAccommodation(accommodation)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateListing(accommodation: Accommodation): Result<Unit> {
        return try {
            accommodationDao.updateAccommodation(accommodation)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteListing(accommodation: Accommodation): Result<Unit> {
        return try {
            accommodationDao.deleteAccommodation(accommodation)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getProviderListings(providerId: Long): Flow<List<Accommodation>> {
        return accommodationDao.getAccommodationsByProvider(providerId)
    }

    fun getAllAvailableListings(): Flow<List<Accommodation>> {
        return accommodationDao.getAvailableAccommodations()
    }

    fun getStudentReservations(studentId: Long): Flow<List<Accommodation>> {
        return accommodationDao.getReservationsByStudent(studentId)
    }

    suspend fun getListingById(id: Long): Accommodation? {
        return accommodationDao.getAccommodationById(id)
    }

    fun filterByPriceRange(min: Double, max: Double): Flow<List<Accommodation>> {
        return accommodationDao.filterByPrice(min, max)
    }

    fun filterByLocation(location: String): Flow<List<Accommodation>> {
        return accommodationDao.filterByLocation(location)
    }

    fun filterByAvailabilityDate(date: Long): Flow<List<Accommodation>> {
        return accommodationDao.filterByAvailabilityDate(date)
    }

    fun filterCombined(minPrice: Double, maxPrice: Double, location: String, date: Long): Flow<List<Accommodation>> {
        return accommodationDao.filterCombined(minPrice, maxPrice, location, date)
    }

    /**
     * Attempts to reserve an accommodation.
     * Returns a Result containing a reference number if successful.
     */
    suspend fun reserveAccommodation(accommodationId: Long, studentId: Long): Result<String> {
        return try {
            val rowsAffected = accommodationDao.reserveAccommodation(accommodationId, studentId)
            if (rowsAffected > 0) {
                // Generate a simulated reference number
                val ref = "REF-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
                Result.success(ref)
            } else {
                Result.failure(Exception("Accommodation is no longer available or already reserved."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
