package com.example.studentaccomfinder.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * SessionManager — saves logged-in user session
 *
 * Why SharedPreferences?
 * - Persists small data (user ID, role) across app restarts
 * - Fast read/write (no database query needed)
 * - Cleared on logout
 *
 * Security note: In production, use EncryptedSharedPreferences!
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME,
        Context.MODE_PRIVATE
    )

    /**
     * Save user session after successful login
     */
    fun saveUserSession(userId: Long, role: String) {
        prefs.edit().apply {
            putLong(Constants.KEY_LOGGED_IN_USER_ID, userId)
            putString(Constants.KEY_USER_ROLE, role)
            apply()  // apply() is async (faster), commit() is synchronous
        }
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return getUserId() != -1L
    }

    /**
     * Get logged-in user ID
     * Returns -1 if not logged in
     */
    fun getUserId(): Long {
        return prefs.getLong(Constants.KEY_LOGGED_IN_USER_ID, -1L)
    }

    /**
     * Get logged-in user role
     * Returns null if not logged in
     */
    fun getUserRole(): String? {
        return prefs.getString(Constants.KEY_USER_ROLE, null)
    }

    /**
     * Clear session on logout
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}