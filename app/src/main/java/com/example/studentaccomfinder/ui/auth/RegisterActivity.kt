package com.example.studentaccomfinder.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.studentaccomfinder.R
import com.example.studentaccomfinder.data.local.entity.User
import com.example.studentaccomfinder.databinding.ActivityRegisterBinding
import com.example.studentaccomfinder.utils.Constants
import com.example.studentaccomfinder.viewmodel.AuthViewModel

/**
 * RegisterActivity — User registration screen
 *
 * Features:
 * - Role selection (Student/Provider)
 * - Dynamic fields (Student ID vs Company Name)
 * - Input validation
 * - Password confirmation check
 * - Navigate to Login or Dashboard on success
 */
class RegisterActivity : AppCompatActivity() {

    // ViewBinding — type-safe access to views (no findViewById!)
    private lateinit var binding: ActivityRegisterBinding

    // ViewModel — survives screen rotation, manages data
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleSelection()
        setupClickListeners()
        observeViewModel()
    }

    /**
     * Show/hide fields based on selected role
     * Student → show Student ID field
     * Provider → show Company Name field
     */
    private fun setupRoleSelection() {
        binding.radioGroupRole.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.radioStudent.id -> {
                    binding.tilStudentId.visibility = View.VISIBLE
                    binding.tilCompanyName.visibility = View.GONE
                }
                binding.radioProvider.id -> {
                    binding.tilStudentId.visibility = View.GONE
                    binding.tilCompanyName.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Set up button click handlers
     */
    private fun setupClickListeners() {
        // Register button
        binding.btnRegister.setOnClickListener {
            attemptRegistration()
        }

        // Navigate to Login
        binding.tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()  // Close register screen
        }
    }

    /**
     * Collect input, validate, and submit to ViewModel
     */
    private fun attemptRegistration() {
        // Get values from fields
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        // Determine role
        val role = when (binding.radioGroupRole.checkedRadioButtonId) {
            binding.radioStudent.id -> Constants.ROLE_STUDENT
            else -> Constants.ROLE_PROVIDER
        }

        // Role-specific fields
        val studentId = if (role == Constants.ROLE_STUDENT) {
            binding.etStudentId.text.toString().trim()
        } else null

        val companyName = if (role == Constants.ROLE_PROVIDER) {
            binding.etCompanyName.text.toString().trim()
        } else null

        // Validation: basic check
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        // Validation: password match
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = getString(R.string.error_password_mismatch)
            return
        }

        // Create User entity
        val user = User(
            fullName = fullName,
            email = email,
            phoneNumber = phone,
            password = password,
            role = role,
            studentId = studentId,
            companyName = companyName
        )

        // Submit to ViewModel
        viewModel.registerUser(user)
    }

    /**
     * Observe LiveData from ViewModel
     * UI automatically updates when data changes
     */
    private fun observeViewModel() {
        // Loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
        }

        // Registration result
        viewModel.registrationResult.observe(this) { result ->
            result.fold(
                onSuccess = { userId ->
                    Toast.makeText(this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show()
                    // Navigate to Login after successful registration
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                onFailure = { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}