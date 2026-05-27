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
        adapter = AccommodationAdapter { accommodation ->
            onAccommodationClicked(accommodation)
        }

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
            // ProgressBar removed - use btnGenerateData enabled state instead
            binding.btnGenerateData.isEnabled = !isLoading
        }

        // Filtered results (used after applying filters)
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

        // Update count
        Toast.makeText(this, "${listings.size} listings found", Toast.LENGTH_SHORT).show()
    }

    /**
     * Handle accommodation item click
     */
    private fun onAccommodationClicked(accommodation: Accommodation) {
        Toast.makeText(
            this,
            "Selected: ${accommodation.title}\nPrice: P${accommodation.price}",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Show filter bottom sheet dialog
     */
    private fun showFilterBottomSheet() {
        val filterSheet = FilterBottomSheet(viewModel) {
            // Callback when filters applied or cleared
        }
        filterSheet.show(supportFragmentManager, "FilterBottomSheet")
    }

    /**
     * Generate sample data for demo
     */
    private fun generateSampleData() {
        SampleDataGenerator.generateSampleListings(this)
        Toast.makeText(this, "Generating 50 sample listings...", Toast.LENGTH_SHORT).show()

        // Refresh after short delay to allow database insertion
        binding.recyclerView.postDelayed({
            recreate()
        }, 1000)
    }

    /**
     * Handle back press - FIXED: Added super call
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()  // ✅ FIXED: Added super call
        finish()
    }
}