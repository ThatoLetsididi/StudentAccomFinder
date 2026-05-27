package com.example.studentaccomfinder.ui.provider

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.studentaccomfinder.R
import com.example.studentaccomfinder.databinding.ActivityProviderDashboardBinding
import com.example.studentaccomfinder.ui.auth.LoginActivity
import com.example.studentaccomfinder.utils.SessionManager
import android.widget.Toast

/**
 * ProviderDashboardActivity — Main screen for providers after login
 *
 * Features:
 * - Welcome message with provider/company name
 * - Manage listings button
 * - Add new listing button
 * - Logout functionality
 */
class ProviderDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProviderDashboardBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProviderDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.tvWelcome.text = getString(R.string.welcome_provider)
        binding.tvSubtitle.text = getString(R.string.provider_dashboard_subtitle)
    }

    private fun setupClickListeners() {
        // Manage Listings — navigate to ManageListingsActivity
        binding.cardManage.setOnClickListener {
            // TODO: Create ManageListingsActivity in future step
            Toast.makeText(this, "Manage Listings - Coming soon", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, ManageListingsActivity::class.java))
        }

        // Add New Listing — navigate to AddListingActivity
        binding.cardAdd.setOnClickListener {
            startActivity(Intent(this, AddListingActivity::class.java))
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}