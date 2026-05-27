package com.example.studentaccomfinder.ui.provider

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.studentaccomfinder.data.local.entity.Accommodation
import com.example.studentaccomfinder.databinding.ActivityAddListingBinding
import com.example.studentaccomfinder.utils.Constants
import com.example.studentaccomfinder.utils.GlideHelper
import com.example.studentaccomfinder.utils.SessionManager
import com.example.studentaccomfinder.viewmodel.AccommodationViewModel
import java.util.Calendar

/**
 * AddListingActivity — Provider creates new accommodation listing
 */
class AddListingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddListingBinding
    private val viewModel: AccommodationViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    private var selectedDate: Long = System.currentTimeMillis()
    private var selectedImageName: String = "house_1"  // Default image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupUI()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupUI() {
        // Load default image preview
        GlideHelper.loadAccommodationImage(this, binding.ivPreview, selectedImageName)

        // Set default date to today
        updateDateDisplay()
    }

    private fun setupClickListeners() {
        // Date picker
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        // Image selection (cycle through sample images for demo)
        binding.btnSelectImage.setOnClickListener {
            cycleSampleImage()
        }

        // Submit button
        binding.btnSubmit.setOnClickListener {
            submitListing()
        }

        // Cancel button
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.timeInMillis
                updateDateDisplay()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateDisplay() {
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
        val dateStr = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        binding.tvSelectedDate.text = "Available from: $dateStr"
    }

    private fun cycleSampleImage() {
        // Cycle through house_1 to house_10 for demo
        val currentNum = selectedImageName.removePrefix("house_").toIntOrNull() ?: 1
        val nextNum = if (currentNum >= 10) 1 else currentNum + 1
        selectedImageName = "house_$nextNum"
        GlideHelper.loadAccommodationImage(this, binding.ivPreview, selectedImageName)
    }

    private fun submitListing() {
        val providerId = sessionManager.getUserId()
        if (providerId == -1L) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
            return
        }

        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val type = binding.etType.text.toString().trim()
        val amenities = binding.etAmenities.text.toString().trim()
        val depositStr = binding.etDeposit.text.toString().trim()

        // Validation
        if (title.isEmpty() || priceStr.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0
        val deposit = depositStr.toDoubleOrNull() ?: (price * Constants.MIN_DEPOSIT_PERCENTAGE)

        val accommodation = Accommodation(
            providerId = providerId,
            title = title,
            description = description,
            price = price,
            location = location,
            type = type.ifEmpty { "Apartment" },
            amenities = amenities,
            availabilityDate = selectedDate,
            depositAmount = deposit,
            imageName = selectedImageName,
            latitude = Constants.BAC_LATITUDE,  // Default to campus for demo
            longitude = Constants.BAC_LONGITUDE
        )

        viewModel.addListing(accommodation)
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSubmit.isEnabled = !isLoading
        }

        viewModel.addResult.observe(this) { result ->
            result.fold(
                onSuccess = {
                    Toast.makeText(this, "Listing created successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onFailure = { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}