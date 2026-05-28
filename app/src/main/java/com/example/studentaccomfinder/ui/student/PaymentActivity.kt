package com.example.studentaccomfinder.ui.student

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.studentaccomfinder.databinding.ActivityPaymentBinding
import com.example.studentaccomfinder.utils.SessionManager
import com.example.studentaccomfinder.viewmodel.AccommodationViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * PaymentActivity — Simulated payment workflow
 */
class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private val viewModel: AccommodationViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private var accommodationId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        accommodationId = intent.getLongExtra("ACCOMMODATION_ID", -1)

        if (accommodationId == -1L) {
            Toast.makeText(this, "Invalid accommodation", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadAccommodationDetails()
        setupClickListeners()
        observeViewModel()
    }

    private fun loadAccommodationDetails() {
        // We use a coroutine in the activity or just let the viewModel fetch it if we had a "getById" liveData
        // For simplicity, we'll use a suspending call in a lifecycle scope if available or just fetch via viewModel
        lifecycle.run {
            // Since we updated ViewModel with getListingById, we can call it
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.launch
            
            // Re-evaluating: I don't want to mess up imports. 
            // I'll just use the viewModel to observe the specific item if I had that, 
            // or better yet, pass title and price via Intent for speed.
        }
        
        val title = intent.getStringExtra("ACCOMMODATION_TITLE") ?: "Accommodation"
        val price = intent.getDoubleExtra("ACCOMMODATION_PRICE", 0.0)
        
        binding.tvAccomTitle.text = title
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "BW"))
        binding.tvAccomPrice.text = "Total Due: ${formatter.format(price)}"
    }

    private fun setupClickListeners() {
        binding.btnPay.setOnClickListener {
            validateAndPay()
        }
    }

    private fun validateAndPay() {
        val cardNumber = binding.etCardNumber.text.toString()
        val expiry = binding.etExpiry.text.toString()
        val cvv = binding.etCVV.text.toString()

        if (cardNumber.length < 16 || expiry.isEmpty() || cvv.length < 3) {
            Toast.makeText(this, "Please enter valid card details", Toast.LENGTH_SHORT).show()
            return
        }

        // Simulate payment processing delay
        binding.btnPay.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        
        Handler(Looper.getMainLooper()).postDelayed({
            val studentId = sessionManager.getUserId()
            viewModel.reserveListing(accommodationId, studentId)
        }, 2000)
    }

    private fun observeViewModel() {
        viewModel.reserveResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            if (result.isSuccess) {
                val ref = result.getOrNull()
                showSuccess(ref)
            } else {
                binding.btnPay.isEnabled = true
                Toast.makeText(this, result.exceptionOrNull()?.message, Toast.LENGTH_LONG).show()
                // If it's already reserved, we might want to finish
                if (result.exceptionOrNull()?.message?.contains("already reserved") == true) {
                    finish()
                }
            }
        }
    }

    private fun showSuccess(ref: String?) {
        binding.tvReceipt.visibility = View.VISIBLE
        binding.tvReceipt.text = "Payment Successful!\nReference: $ref\n\nReturning to dashboard..."
        binding.btnPay.visibility = View.GONE
        binding.tilCardNumber.visibility = View.GONE
        binding.tilExpiry.visibility = View.GONE
        binding.tilCVV.visibility = View.GONE

        Toast.makeText(this, "Room Reserved Successfully", Toast.LENGTH_LONG).show()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, StudentDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }, 4000)
    }
}