package com.example.studentaccomfinder.utils

object Constants {

    // ═══════════════════════════════════════════════════════
    // BAC FRANCISTOWN CAMPUS coordinates (confirmed with lecturer)
    // Used in Step 9 for distance calculation
    // ═══════════════════════════════════════════════════════
    const val BAC_LATITUDE = -21.1669
    const val BAC_LONGITUDE = 27.5153

    // ═══════════════════════════════════════════════════════
    // User roles — stored in the database to distinguish user types
    // CHANGED: "LANDLORD" → "PROVIDER" to match assignment terminology
    // ═══════════════════════════════════════════════════════
    const val ROLE_STUDENT = "STUDENT"
    const val ROLE_PROVIDER = "PROVIDER"

    // ═══════════════════════════════════════════════════════
    // Reservation status values
    // ═══════════════════════════════════════════════════════
    const val STATUS_AVAILABLE = "AVAILABLE"
    const val STATUS_RESERVED = "RESERVED"

    // ═══════════════════════════════════════════════════════
    // Database name
    // ═══════════════════════════════════════════════════════
    const val DATABASE_NAME = "accom_finder_db"

    // ═══════════════════════════════════════════════════════
    // Image-related constants (UPDATED for local drawables)
    // ═══════════════════════════════════════════════════════
    const val MAX_IMAGES_PER_ACCOMMODATION = 1  // At least one image per listing (assignment req)

    // Local drawable resources for demo reliability (50 sample listings)
    // Naming convention: house_1, house_2, ... house_50
    const val DRAWABLE_PREFIX = "house_"

    // Placeholder and error images (built-in Android drawables as fallback)
    const val PLACEHOLDER_IMAGE_RES = android.R.drawable.ic_menu_gallery
    const val ERROR_IMAGE_RES = android.R.drawable.ic_menu_close_clear_cancel

    // ═══════════════════════════════════════════════════════
    // Preference keys for SharedPreferences
    // ═══════════════════════════════════════════════════════
    const val PREFS_NAME = "accom_finder_prefs"
    const val KEY_LOGGED_IN_USER_ID = "logged_in_user_id"
    const val KEY_USER_ROLE = "user_role"

    // ═══════════════════════════════════════════════════════
    // Payment simulation constants
    // ═══════════════════════════════════════════════════════
    const val PAYMENT_REFERENCE_PREFIX = "BAC-ACCOM-"
    const val MIN_DEPOSIT_PERCENTAGE = 0.1  // 10% minimum deposit
}