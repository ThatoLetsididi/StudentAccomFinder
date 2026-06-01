package com.example.studentaccomfinder.ui.provider

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studentaccomfinder.data.local.entity.Accommodation
import com.example.studentaccomfinder.databinding.ActivityAddListingBinding
import com.example.studentaccomfinder.utils.Constants
import com.example.studentaccomfinder.utils.GlideHelper
import com.example.studentaccomfinder.utils.SessionManager
import com.example.studentaccomfinder.viewmodel.AccommodationViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * AddListingActivity — Provider creates or edits accommodation listing
 * Now supports real image selection from gallery.
 */
class AddListingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddListingBinding
    private val viewModel: AccommodationViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    private var selectedDate: Long = System.currentTimeMillis()
    private var selectedImageSource: String? = "house_1" // Default image or URI string
    private var editingAccommodation: Accommodation? = null

    // Photo Picker Launcher
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // Persist permission for local URI if needed (simplified for this demo)
            contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedImageSource = uri.toString()
            GlideHelper.loadAccommodationImage(this, binding.ivPreview, selectedImageSource)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupUI()
        setupClickListeners()
        observeViewModel()

        // Check if we are in Edit Mode
        val accommodationId = intent.getLongExtra("ACCOMMODATION_ID", -1L)
        if (accommodationId != -1L) {
            loadAccommodationForEdit(accommodationId)
        }
    }

    private fun setupUI() {
        // Load default image preview
        GlideHelper.loadAccommodationImage(this, binding.ivPreview, selectedImageSource)
        updateDateDisplay()
    }

    private fun loadAccommodationForEdit(id: Long) {
        lifecycleScope.launch {
            val accommodation = viewModel.getListingById(id)
            if (accommodation != null) {
                editingAccommodation = accommodation
                populateFields(accommodation)
            } else {
                Toast.makeText(this@AddListingActivity, "Listing not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun populateFields(accommodation: Accommodation) {
        binding.tvTitle.text = "Edit Listing"
        binding.btnSubmit.text = "Update Listing"
        
        binding.etTitle.setText(accommodation.title)
        binding.etDescription.setText(accommodation.description)
        binding.etPrice.setText(accommodation.price.toString())
        binding.etLocation.setText(accommodation.location)
        binding.etType.setText(accommodation.type)
        binding.etAmenities.setText(accommodation.amenities)
        binding.etDeposit.setText(accommodation.depositAmount.toString())
        
        selectedDate = accommodation.availabilityDate
        updateDateDisplay()
        
        selectedImageSource = accommodation.imageName
        GlideHelper.loadAccommodationImage(this, binding.ivPreview, selectedImageSource)
    }

    private fun setupClickListeners() {
        // Date picker
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        // Real Gallery Image Selection
        binding.btnSelectImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
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

        if (title.isEmpty() || priceStr.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0
        val deposit = depositStr.toDoubleOrNull() ?: (price * Constants.MIN_DEPOSIT_PERCENTAGE)

        val accommodation = editingAccommodation?.copy(
            title = title,
            description = description,
            price = price,
            location = location,
            type = type.ifEmpty { "Apartment" },
            amenities = amenities,
            availabilityDate = selectedDate,
            depositAmount = deposit,
            imageName = selectedImageSource
        ) ?: Accommodation(
            providerId = providerId,
            title = title,
            description = description,
            price = price,
            location = location,
            type = type.ifEmpty { "Apartment" },
            amenities = amenities,
            availabilityDate = selectedDate,
            depositAmount = deposit,
            imageName = selectedImageSource,
            latitude = Constants.BAC_LATITUDE,
            longitude = Constants.BAC_LONGITUDE
        )

        if (editingAccommodation != null) {
            viewModel.updateListing(accommodation)
        } else {
            viewModel.addListing(accommodation)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSubmit.isEnabled = !isLoading
        }

        viewModel.addResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Listing created successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.updateResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Listing updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}