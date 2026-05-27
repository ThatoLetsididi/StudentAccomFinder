package com.example.studentaccomfinder.data.repository

import com.example.studentaccomfinder.data.local.AppDatabase
import com.example.studentaccomfinder.data.local.entity.User

/**
 * UserRepository — single source of truth for user data
 *
 * Why Repository?
 * - Abstracts data source (Room) from ViewModels
 * - UI never touches database directly
 * - Easy to swap Room for API later without changing UI code
 * - Centralizes data operations and validation logic
 */
class UserRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()

    /**
     * Register a new user
     *
     * Returns: Result.success(userId) or Result.failure(exception)
     *
     * Defensive checks:
     * - Email must not be empty
     * - Password must be at least 6 characters
     * - Email must not already exist
     * - Role must be valid (STUDENT or PROVIDER)
     */
    suspend fun registerUser(user: User): Result<Long> {
        return try {
            // Validation: empty fields
            if (user.email.isBlank() || user.password.isBlank() || user.fullName.isBlank()) {
                return Result.failure(Exception("All fields are required"))
            }

            // Validation: password length
            if (user.password.length < 6) {
                return Result.failure(Exception("Password must be at least 6 characters"))
            }

            // Validation: email format (basic check)
            if (!user.email.contains("@") || !user.email.contains(".")) {
                return Result.failure(Exception("Invalid email format"))
            }

            // Validation: check if email already exists
            val existingCount = userDao.emailExists(user.email)
            if (existingCount > 0) {
                return Result.failure(Exception("Email already registered"))
            }

            // Validation: role must be valid
            if (user.role != "STUDENT" && user.role != "PROVIDER") {
                return Result.failure(Exception("Invalid role selected"))
            }

            // Insert user and return generated ID
            val userId = userDao.insertUser(user)
            Result.success(userId)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Authenticate user (login)
     *
     * Returns: User object if credentials match, null if not found
     */
    suspend fun login(email: String, password: String): User? {
        // Validation: empty fields
        if (email.isBlank() || password.isBlank()) {
            return null
        }

        return userDao.authenticate(email, password)
    }

    /**
     * Get user by ID
     * Used to load user data across the app
     */
    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }

    /**
     * Check if email exists (for validation)
     */
    suspend fun emailExists(email: String): Boolean {
        return userDao.emailExists(email) > 0
    }
}