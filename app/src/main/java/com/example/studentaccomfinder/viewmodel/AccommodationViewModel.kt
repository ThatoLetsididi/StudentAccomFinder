package com.example.studentaccomfinder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.studentaccomfinder.data.local.AppDatabase
import com.example.studentaccomfinder.data.local.entity.Accommodation
import com.example.studentaccomfinder.data.repository.AccommodationRepository
import kotlinx.coroutines.launch

/**
 * AccommodationViewModel — manages accommodation data and filtering
 */
class AccommodationViewModel(application: Application) : AndroidViewModel(application) {

    // ═══════════════════════════════════════════════════════
    // Filter Criteria Data Class
    // ═══════════════════════════════════════════════════════
    /**
     * Holds current filter values
     */
    data class FilterCriteria(
        val minPrice: Double = 0.0,
        val maxPrice: Double = 10000.0,
        val location: String = "",
        val availabilityDate: Long = Long.MAX_VALUE
    )

    // ═══════════════════════════════════════════════════════
    // Repository
    // ═══════════════════════════════════════════════════════
    private val repository: AccommodationRepository

    // ═══════════════════════════════════════════════════════
    // Loading State
    // ═══════════════════════════════════════════════════════
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // ═══════════════════════════════════════════════════════
    // Results
    // ═══════════════════════════════════════════════════════
    private val _addResult = MutableLiveData<Result<Long>>()
    val addResult: LiveData<Result<Long>> = _addResult

    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    private val _reserveResult = MutableLiveData<Result<String>>()
    val reserveResult: LiveData<Result<String>> = _reserveResult

    // ═══════════════════════════════════════════════════════
    // Filter State
    // ═══════════════════════════════════════════════════════
    private val _currentFilters = MutableLiveData<FilterCriteria>()
    val currentFilters: LiveData<FilterCriteria> = _currentFilters

    private val _filterResult = MutableLiveData<List<Accommodation>>()
    val filterResult: LiveData<List<Accommodation>> = _filterResult

    // ═══════════════════════════════════════════════════════
    // Initialization
    // ═══════════════════════════════════════════════════════
    init {
        val database = AppDatabase.getDatabase(application)
        repository = AccommodationRepository(database)

        // Initialize with default filters (show all)
        _currentFilters.value = FilterCriteria()
    }

    // ═══════════════════════════════════════════════════════
    // CRUD Operations
    // ═══════════════════════════════════════════════════════

    fun addListing(accommodation: Accommodation) {
        viewModelScope.launch {
            _isLoading.value = true
            _addResult.value = repository.addListing(accommodation)
            _isLoading.value = false
        }
    }

    fun updateListing(accommodation: Accommodation) {
        viewModelScope.launch {
            _isLoading.value = true
            _updateResult.value = repository.updateListing(accommodation)
            _isLoading.value = false
        }
    }

    fun deleteListing(accommodation: Accommodation) {
        viewModelScope.launch {
            _isLoading.value = true
            _deleteResult.value = repository.deleteListing(accommodation)
            _isLoading.value = false
        }
    }

    fun reserveListing(accommodationId: Long, studentId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _reserveResult.value = repository.reserveAccommodation(accommodationId, studentId)
            _isLoading.value = false
        }
    }

    // ═══════════════════════════════════════════════════════
    // Data Retrieval (Flow converted to LiveData)
    // ═══════════════════════════════════════════════════════

    /**
     * Get all available listings as LiveData
     */
    fun getAvailableListingsLiveData(): LiveData<List<Accommodation>> {
        return repository.getAllAvailableListings().asLiveData()
    }

    /**
     * Get provider's listings
     */
    fun getProviderListingsLiveData(providerId: Long): LiveData<List<Accommodation>> {
        return repository.getProviderListings(providerId).asLiveData()
    }

    /**
     * Get student's reserved listings
     */
    fun getStudentReservationsLiveData(studentId: Long): LiveData<List<Accommodation>> {
        return repository.getStudentReservations(studentId).asLiveData()
    }

    /**
     * Get single listing by ID
     */
    suspend fun getListingById(id: Long): Accommodation? {
        return repository.getListingById(id)
    }

    // ═══════════════════════════════════════════════════════
    // Filtering Operations
    // ═══════════════════════════════════════════════════════

    /**
     * Apply filters and update filterResult LiveData
     */
    fun applyFilters(criteria: FilterCriteria) {
        _currentFilters.value = criteria

        viewModelScope.launch {
            _isLoading.value = true

            repository.filterCombined(
                minPrice = criteria.minPrice,
                maxPrice = criteria.maxPrice,
                location = criteria.location,
                date = criteria.availabilityDate
            ).collect { listings ->
                _filterResult.postValue(listings)
                _isLoading.value = false
            }
        }
    }

    /**
     * Get filtered results as LiveData
     */
    fun getFilteredListingsLiveData(): LiveData<List<Accommodation>> {
        return filterResult
    }

    /**
     * Clear all filters and show all available listings
     */
    fun clearFilters() {
        _currentFilters.value = FilterCriteria()

        viewModelScope.launch {
            repository.getAllAvailableListings().collect { listings ->
                _filterResult.postValue(listings)
            }
        }
    }
}