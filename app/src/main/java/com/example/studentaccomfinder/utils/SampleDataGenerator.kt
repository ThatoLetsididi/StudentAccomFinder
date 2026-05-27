package com.example.studentaccomfinder.utils

import android.content.Context
import com.example.studentaccomfinder.data.local.AppDatabase
import com.example.studentaccomfinder.data.local.entity.Accommodation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * SampleDataGenerator — creates 50 sample listings for demo
 *
 * Why? Assignment requires 50 different types of houses as records
 */
object SampleDataGenerator {

    private val locations = listOf(
        "Riverside", "Kgaphamadi", "Blue-Town", "White-City",
        "Block 1", "Block 2", "Shashe", "Gerald Estates",
        "Block 3", "Block 4", "Block 5", "Block 6",
    )

    private val types = listOf(
        "Apartment", "Hostel", "Flat", "Studio", "Shared House",
        "Single Room", "Bachelor Pad", "Townhouse", "Cottage", "Dormitory"
    )

    private val amenitiesList = listOf(
        "WiFi, Parking, Laundry",
        "WiFi, Pool, Gym, Security",
        "Parking, Garden, Water included",
        "WiFi, Furnished, Security",
        "Laundry, Kitchen, Study room",
        "Pool, Parking, Air conditioning",
        "WiFi, Gym, 24/7 Security",
        "Furnished, Water, Electricity included",
        "Garden, Parking, Braai area",
        "WiFi, Laundry, Cleaning service"
    )

    private val titles = listOf(
        "Modern 2-Bedroom Apartment",
        "Cozy Studio Near Campus",
        "Spacious Shared House",
        "Luxury Hostel with Pool",
        "Affordable Single Room",
        "Furnished Bachelor Pad",
        "Secure Townhouse Complex",
        "Quiet Cottage for Students",
        "Newly Renovated Flat",
        "Premium Dormitory Block"
    )

    fun generateSampleListings(context: Context) {
        val database = AppDatabase.getDatabase(context)
        val accommodationDao = database.accommodationDao()

        CoroutineScope(Dispatchers.IO).launch {
            // Create 50 sample listings
            for (i in 1..50) {
                val accommodation = Accommodation(
                    providerId = 1, // Default provider ID
                    title = "${titles[i % titles.size]} #$i",
                    description = "Beautiful accommodation perfect for students. " +
                            "Located in a safe neighborhood with easy access to campus.",
                    price = (1500 + (i * 100)).toDouble(), // P1500 to P6500
                    location = locations[i % locations.size],
                    type = types[i % types.size],
                    amenities = amenitiesList[i % amenitiesList.size],
                    availabilityDate = System.currentTimeMillis() + (i * 86400000), // Future dates
                    depositAmount = (1500 + (i * 100)).toDouble() * 0.1, // 10% deposit
                    imageName = GlideHelper.getSampleImageName((i % 15) + 1), // Cycle through house_1 to house_15
                    latitude = Constants.BAC_LATITUDE + (kotlin.random.Random.nextDouble() - 0.5) * 0.1,
                    longitude = Constants.BAC_LONGITUDE + (kotlin.random.Random.nextDouble() - 0.5) * 0.1,
                    status = Constants.STATUS_AVAILABLE
                )

                try {
                    accommodationDao.insertAccommodation(accommodation)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}