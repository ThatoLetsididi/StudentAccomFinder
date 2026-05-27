package com.example.studentaccomfinder.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.studentaccomfinder.R
import com.example.studentaccomfinder.databinding.ActivityLoginBinding
import com.example.studentaccomfinder.utils.Constants
import com.example.studentaccomfinder.viewmodel.AuthViewModel
import com.example.studentaccomfinder.MainActivity

/**
 * LoginActivity — User login screen
 *
 * Features:
 * - Email and password authentication
 * - Navigate to role-specific dashboard
 * - Link to registration
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        // Login button
        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }

        // Navigate to Register
        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(email, password)
    }

    private fun observeViewModel() {
        // Loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
        }

        // Login result
        viewModel.loginResult.observe(this) { result ->
            result.fold(
                onSuccess = { user ->
                    Toast.makeText(this, getString(R.string.welcome_user, user.fullName), Toast.LENGTH_SHORT).show()

                    val intent = when (user.role) {
                        Constants.ROLE_STUDENT -> {
                            // ✅ Navigate to Student Dashboard
                            Intent(this, com.example.studentaccomfinder.ui.student.StudentDashboardActivity::class.java)
                        }
                        Constants.ROLE_PROVIDER -> {
                            // ✅ Navigate to Provider Dashboard
                            Intent(this, com.example.studentaccomfinder.ui.provider.ProviderDashboardActivity::class.java)
                        }
                        else -> Intent(this, MainActivity::class.java)
                    }

                    startActivity(intent)
                    finish()
                },
                onFailure = { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}