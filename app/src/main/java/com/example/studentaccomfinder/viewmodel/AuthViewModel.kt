package com.example.studentaccomfinder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studentaccomfinder.data.local.AppDatabase
import com.example.studentaccomfinder.data.local.entity.User
import com.example.studentaccomfinder.data.repository.UserRepository
import com.example.studentaccomfinder.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * AuthViewModel — manages authentication state
 *
 * Why AndroidViewModel (not just ViewModel)?
 * - Needs Application context for database and SharedPreferences
 * - Survives screen rotation (configuration changes)
 *
 * LiveData:
 * - _registrationResult = private MutableLiveData (only ViewModel modifies)
 * - registrationResult = public LiveData (UI observes, cannot modify)
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Repository instance
    private val repository: UserRepository

    // Session manager for saving login state
    private val sessionManager: SessionManager

    // LiveData for registration result (UI observes this)
    private val _registrationResult = MutableLiveData<Result<Long>>()
    val registrationResult: LiveData<Result<Long>> = _registrationResult

    // LiveData for login result
    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    // Loading state (show/hide progress bar)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        // Initialize database and repository
        val database = AppDatabase.getDatabase(application)
        repository = UserRepository(database)
        sessionManager = SessionManager(application)
    }

    /**
     * Register a new user
     * Called from RegisterActivity when user clicks "Sign Up"
     */
    fun registerUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true

            // Call repository (background thread via suspend)
            val result = repository.registerUser(user)

            // Post result to LiveData (automatically updates UI)
            _registrationResult.value = result

            _isLoading.value = false
        }
    }

    /**
     * Login user
     * Called from LoginActivity when user clicks "Login"
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val user = repository.login(email, password)

            if (user != null) {
                // Save session for auto-login next time
                sessionManager.saveUserSession(user.id, user.role, user.fullName)
                _loginResult.value = Result.success(user)
            } else {
                _loginResult.value = Result.failure(
                    Exception("Invalid email or password")
                )
            }

            _isLoading.value = false
        }
    }

    /**
     * Check if user is already logged in (for splash/auto-login)
     */
    fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    /**
     * Get saved user role to navigate to correct dashboard
     */
    fun getSavedUserRole(): String? {
        return sessionManager.getUserRole()
    }

    /**
     * Logout — clear session
     */
    fun logout() {
        sessionManager.clearSession()
    }
}