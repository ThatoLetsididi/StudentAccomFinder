package com.example.studentaccomfinder.ui.student

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentaccomfinder.data.local.entity.Accommodation
import com.example.studentaccomfinder.databinding.ActivityBrowseListingsBinding
import com.example.studentaccomfinder.ui.chat.ChatActivity
import com.example.studentaccomfinder.utils.SampleDataGenerator
import com.example.studentaccomfinder.utils.SessionManager
import com.example.studentaccomfinder.viewmodel.AccommodationViewModel

/**
 * BrowseListingsActivity — Student browses and filters accommodations
 */
class BrowseListingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBrowseListingsBinding
    private val viewModel: AccommodationViewModel by viewModels()
    private lateinit var adapter: AccommodationAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowseListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()

        // Initially load all available listings
        loadAllListings()
    }

    /**
     * Setup RecyclerView with AccommodationAdapter
     */
    private fun setupRecyclerView() {
        adapter = AccommodationAdapter(
            onItemClick = { accommodation ->
                onAccommodationClicked(accommodation)
            },
            onChatClick = { accommodation ->
                openChat(accommodation)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
    }

    /**
     * Setup all click listeners
     */
    private fun setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Filter FAB
        binding.fabFilter.setOnClickListener {
            showFilterBottomSheet()
        }

        // Generate sample data button
        binding.btnGenerateData.setOnClickListener {
            generateSampleData()
        }
    }

    /**
     * Observe LiveData from ViewModel
     */
    private fun observeViewModel() {
        // Loading state
        viewModel.isLoading.observe(this) { isLoading ->
            // Use btnGenerateData enabled state
            binding.btnGenerateData.isEnabled = !isLoading
        }

        // Filtered results
        viewModel.filterResult.observe(this) { listings ->
            updateListingsUI(listings)
        }
    }

    /**
     * Load all available listings (no filters)
     */
    private fun loadAllListings() {
        viewModel.getAvailableListingsLiveData().observe(this) { listings ->
            updateListingsUI(listings)
        }
    }

    /**
     * Update UI based on listings data
     */
    private fun updateListingsUI(listings: List<Accommodation>) {
        if (listings.isEmpty()) {
            showEmptyState("No accommodations available.\nTap 'Generate Sample Data' for demo.")
        } else {
            showListings(listings)
        }
    }

    /**
     * Show empty state message
     */
    private fun showEmptyState(message: String) {
        binding.tvEmpty.visibility = View.VISIBLE
        binding.tvEmpty.text = message
        binding.recyclerView.visibility = View.GONE
    }

    /**
     * Show listings in RecyclerView
     */
    private fun showListings(listings: List<Accommodation>) {
        binding.tvEmpty.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        adapter.submitList(listings)
    }

    /**
     * Handle accommodation item click - Navigate to Payment
     */
    private fun onAccommodationClicked(accommodation: Accommodation) {
        if (accommodation.status != "AVAILABLE") {
            Toast.makeText(this, "This accommodation is already reserved", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, PaymentActivity::class.java).apply {
            putExtra("ACCOMMODATION_ID", accommodation.id)
            putExtra("ACCOMMODATION_TITLE", accommodation.title)
            putExtra("ACCOMMODATION_PRICE", accommodation.price)
        }
        startActivity(intent)
    }

    /**
     * Open Chat with the provider
     */
    private fun openChat(accommodation: Accommodation) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("RECEIVER_ID", accommodation.providerId)
            putExtra("RECEIVER_NAME", "Provider") // In a real app, you'd fetch the provider name
        }
        startActivity(intent)
    }

    /**
     * Show filter bottom sheet dialog
     */
    private fun showFilterBottomSheet() {
        val filterSheet = FilterBottomSheet(viewModel) {
            // After applying filters, the viewModel.filterResult will be updated
            // and observed in observeViewModel()
        }
        filterSheet.show(supportFragmentManager, "FilterBottomSheet")
    }

    /**
     * Generate sample data for demo
     */
    private fun generateSampleData() {
        SampleDataGenerator.generateSampleListings(this)
        Toast.makeText(this, "Generating sample listings...", Toast.LENGTH_SHORT).show()

        // Refresh after short delay
        binding.recyclerView.postDelayed({
            // Re-observe to refresh list
            loadAllListings()
        }, 1000)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}