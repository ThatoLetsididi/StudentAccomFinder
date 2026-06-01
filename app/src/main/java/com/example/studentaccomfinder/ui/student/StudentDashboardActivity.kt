package com.example.studentaccomfinder.ui.student

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.studentaccomfinder.R
import com.example.studentaccomfinder.databinding.ActivityStudentDashboardBinding
import com.example.studentaccomfinder.ui.auth.LoginActivity
import com.example.studentaccomfinder.ui.chat.InboxActivity
import com.example.studentaccomfinder.utils.SessionManager

/**
 * StudentDashboardActivity — Main screen for students after login
 */
class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDashboardBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        val userName = sessionManager.getUserName()
        binding.tvWelcome.text = if (userName.isNotEmpty()) {
            getString(R.string.welcome_user, userName)
        } else {
            getString(R.string.welcome_student)
        }
        binding.tvSubtitle.text = getString(R.string.student_dashboard_subtitle)
    }

    private fun setupClickListeners() {
        binding.cardBrowse.setOnClickListener {
            startActivity(Intent(this, BrowseListingsActivity::class.java))
        }

        binding.cardReservations.setOnClickListener {
            startActivity(Intent(this, MyReservationsActivity::class.java))
        }

        binding.cardMessages.setOnClickListener {
            startActivity(Intent(this, InboxActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}