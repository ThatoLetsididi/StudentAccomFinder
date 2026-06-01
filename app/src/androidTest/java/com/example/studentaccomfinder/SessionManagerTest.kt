package com.example.studentaccomfinder

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.studentaccomfinder.utils.Constants
import com.example.studentaccomfinder.utils.SessionManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * SessionManagerTest — Instrumented test for SessionManager
 * 
 * Why Instrumented? 
 * SessionManager depends on SharedPreferences, which requires an Android Context.
 * This test runs on an Android device or emulator to ensure real SharedPreferences integration.
 */
@RunWith(AndroidJUnit4::class)
class SessionManagerTest {

    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        sessionManager = SessionManager(context)
        // Clear session before each test to ensure a clean state
        sessionManager.clearSession()
    }

    @Test
    fun testSaveAndRetrieveSession() {
        val userId = 101L
        val role = Constants.ROLE_STUDENT
        val name = "John Doe"

        // 1. Initially should be logged out
        assertFalse("User should not be logged in initially", sessionManager.isLoggedIn())
        assertEquals("Initial user ID should be -1", -1L, sessionManager.getUserId())

        // 2. Save session
        sessionManager.saveUserSession(userId, role, name)

        // 3. Verify data was saved correctly
        assertTrue("User should be logged in after saving session", sessionManager.isLoggedIn())
        assertEquals("Saved user ID should match", userId, sessionManager.getUserId())
        assertEquals("Saved role should match", role, sessionManager.getUserRole())
        assertEquals("Saved name should match", name, sessionManager.getUserName())
    }

    @Test
    fun testClearSession() {
        // 1. Setup a session
        sessionManager.saveUserSession(1L, Constants.ROLE_PROVIDER, "Jane Provider")
        assertTrue(sessionManager.isLoggedIn())

        // 2. Clear session
        sessionManager.clearSession()

        // 3. Verify it's cleared
        assertFalse("User should be logged out after clearing session", sessionManager.isLoggedIn())
        assertEquals("User ID should be reset to -1", -1L, sessionManager.getUserId())
        assertNull("Role should be null after clearing session", sessionManager.getUserRole())
        assertEquals("Name should be empty after clearing session", "", sessionManager.getUserName())
    }
}
