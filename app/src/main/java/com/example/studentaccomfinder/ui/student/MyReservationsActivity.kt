package com.example.studentaccomfinder.ui.student

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentaccomfinder.databinding.ActivityMyReservationsBinding
import com.example.studentaccomfinder.ui.chat.ChatActivity
import com.example.studentaccomfinder.utils.SessionManager
import com.example.studentaccomfinder.viewmodel.AccommodationViewModel

/**
 * MyReservationsActivity — Students can view their reserved accommodations
 */
class MyReservationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyReservationsBinding
    private val viewModel: AccommodationViewModel by viewModels()
    private lateinit var adapter: AccommodationAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReservationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val studentId = sessionManager.getUserId()

        setupRecyclerView()
        setupClickListeners()
        observeViewModel(studentId)
    }

    private fun setupRecyclerView() {
        adapter = AccommodationAdapter(
            onItemClick = { accommodation ->
                // Optionally show details
            },
            onChatClick = { accommodation ->
                val intent = Intent(this, ChatActivity::class.java).apply {
                    putExtra("RECEIVER_ID", accommodation.providerId)
                    putExtra("RECEIVER_NAME", "Provider")
                }
                startActivity(intent)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel(studentId: Long) {
        viewModel.getStudentReservationsLiveData(studentId).observe(this) { reservations ->
            if (reservations.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(reservations)
            }
        }
    }
}