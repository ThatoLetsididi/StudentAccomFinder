package com.example.studentaccomfinder.ui.student

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studentaccomfinder.databinding.BottomSheetFilterBinding
import com.example.studentaccomfinder.viewmodel.AccommodationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar

/**
 * FilterBottomSheet — Bottom sheet dialog for filtering listings
 */
class FilterBottomSheet(
    private val viewModel: AccommodationViewModel,
    private val onApply: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFilterBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: Long = Long.MAX_VALUE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        loadCurrentFilters()
    }

    private fun setupClickListeners() {
        // Date picker
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        // Apply filters
        binding.btnApply.setOnClickListener {
            applyFilters()
        }

        // Clear filters
        binding.btnClear.setOnClickListener {
            viewModel.clearFilters()
            dismiss()
            onApply()
        }
    }

    private fun loadCurrentFilters() {
        val current = viewModel.currentFilters.value
        current?.let { filters ->
            binding.etMinPrice.setText(filters.minPrice.toString())
            binding.etMaxPrice.setText(filters.maxPrice.toString())
            binding.etLocation.setText(filters.location)
            if (filters.availabilityDate != Long.MAX_VALUE) {
                selectedDate = filters.availabilityDate
                updateDateButtonText()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.timeInMillis
                updateDateButtonText()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateButtonText() {
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
        val dateStr = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        binding.btnSelectDate.text = "Available before: $dateStr"
    }

    private fun applyFilters() {
        val minPrice = binding.etMinPrice.text.toString().toDoubleOrNull() ?: 0.0
        val maxPrice = binding.etMaxPrice.text.toString().toDoubleOrNull() ?: 10000.0
        val location = binding.etLocation.text.toString().trim()

        val criteria = AccommodationViewModel.FilterCriteria(
            minPrice = minPrice,
            maxPrice = maxPrice,
            location = location,
            availabilityDate = selectedDate
        )

        viewModel.applyFilters(criteria)
        dismiss()
        onApply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}