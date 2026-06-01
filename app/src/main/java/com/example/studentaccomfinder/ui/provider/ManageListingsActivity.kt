package com.example.studentaccomfinder.ui.provider

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentaccomfinder.data.local.entity.Accommodation
import com.example.studentaccomfinder.databinding.ActivityManageListingsBinding
import com.example.studentaccomfinder.ui.student.AccommodationAdapter
import com.example.studentaccomfinder.utils.SessionManager
import com.example.studentaccomfinder.viewmodel.AccommodationViewModel

/**
 * ManageListingsActivity — Providers can view and manage their own listings
 */
class ManageListingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageListingsBinding
    private val viewModel: AccommodationViewModel by viewModels()
    private lateinit var adapter: AccommodationAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val providerId = sessionManager.getUserId()

        setupRecyclerView()
        setupClickListeners()
        observeViewModel(providerId)
    }

    private fun setupRecyclerView() {
        // Reuse AccommodationAdapter but with a click listener for management actions
        adapter = AccommodationAdapter(
            onItemClick = { accommodation ->
                showActionDialog(accommodation)
            },
            onChatClick = { _ ->
                // Providers typically don't chat with themselves from the management view
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddListingActivity::class.java))
        }
    }

    private fun observeViewModel(providerId: Long) {
        viewModel.getProviderListingsLiveData(providerId).observe(this) { listings ->
            if (listings.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(listings)
            }
        }

        viewModel.deleteResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Listing deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showActionDialog(accommodation: Accommodation) {
        val options = arrayOf("Edit Listing", "Delete Listing")
        AlertDialog.Builder(this)
            .setTitle(accommodation.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openEditActivity(accommodation)
                    1 -> showDeleteConfirmation(accommodation)
                }
            }
            .show()
    }

    private fun openEditActivity(accommodation: Accommodation) {
        val intent = Intent(this, AddListingActivity::class.java).apply {
            putExtra("ACCOMMODATION_ID", accommodation.id)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmation(accommodation: Accommodation) {
        AlertDialog.Builder(this)
            .setTitle("Delete Listing")
            .setMessage("Are you sure you want to delete '${accommodation.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteListing(accommodation)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
