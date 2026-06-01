package com.example.studentaccomfinder

import android.content.Context
import android.content.SharedPreferences
import com.example.studentaccomfinder.utils.Constants
import com.example.studentaccomfinder.utils.SessionManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * SessionManagerLocalTest — Local JUnit test using Mockito

 */
class SessionManagerLocalTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockPrefs: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Mock the SharedPreferences retrieval
        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs)
        
        // Mock the Editor retrieval
        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        
        // Mock the Editor's fluent API
        `when`(mockEditor.putLong(anyString(), anyLong())).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString().takeIf { it != null } ?: "")).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), any())).thenReturn(mockEditor)
        `when`(mockEditor.clear()).thenReturn(mockEditor)

        sessionManager = SessionManager(mockContext)
    }

    @Test
    fun `test saveUserSession calls SharedPreferences correctly`() {
        val userId = 123L
        val role = Constants.ROLE_STUDENT
        val name = "Test User"

        sessionManager.saveUserSession(userId, role, name)

        // Verify that the editor was used to put the correct values
        verify(mockEditor).putLong(Constants.KEY_LOGGED_IN_USER_ID, userId)
        verify(mockEditor).putString(Constants.KEY_USER_ROLE, role)
        verify(mockEditor).putString(Constants.KEY_USER_NAME, name)
        verify(mockEditor).apply()
    }

    @Test
    fun `test isLoggedIn returns true when user id exists`() {
        `when`(mockPrefs.getLong(Constants.KEY_LOGGED_IN_USER_ID, -1L)).thenReturn(123L)

        val result = sessionManager.isLoggedIn()

        assertTrue(result)
        verify(mockPrefs).getLong(Constants.KEY_LOGGED_IN_USER_ID, -1L)
    }

    @Test
    fun `test isLoggedIn returns false when user id is minus one`() {
        `when`(mockPrefs.getLong(Constants.KEY_LOGGED_IN_USER_ID, -1L)).thenReturn(-1L)

        val result = sessionManager.isLoggedIn()

        assertFalse(result)
    }

    @Test
    fun `test clearSession clears and applies`() {
        sessionManager.clearSession()

        verify(mockEditor).clear()
        verify(mockEditor).apply()
    }
}
