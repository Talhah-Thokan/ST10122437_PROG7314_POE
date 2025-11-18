package com.medassist.app.ui.screens.booking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.medassist.app.R
import com.medassist.app.data.api.BookingRequest
import com.medassist.app.data.repository.DataRepository
import com.medassist.app.data.firebase.FirebaseRepository
import com.medassist.app.ui.base.BaseActivity
import com.medassist.app.utils.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookingActivity : BaseActivity() {
    
    private lateinit var backButton: MaterialButton
    private lateinit var bookButton: MaterialButton
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var reasonEditText: TextInputEditText
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var dataRepository: DataRepository
    private lateinit var firebaseRepository: FirebaseRepository
    
    private var doctorName: String = ""
    private var doctorSpecialty: String = ""
    private var doctorId: String = ""
    
    companion object {
        private const val TAG = "BookingActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        
        preferenceManager = PreferenceManager(this)
        dataRepository = DataRepository(this)
        firebaseRepository = FirebaseRepository()
        
        doctorName = intent.getStringExtra("DOCTOR_NAME") ?: ""
        doctorSpecialty = intent.getStringExtra("DOCTOR_SPECIALTY") ?: ""
        doctorId = intent.getStringExtra("DOCTOR_ID") ?: ""
        
        setupViews()
        setupClickListeners()
        populateUserData()
        
        Log.d(TAG, "BookingActivity created for doctor: $doctorName")
    }
    
    private fun setupViews() {
        backButton = findViewById(R.id.backButton)
        bookButton = findViewById(R.id.bookButton)
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        reasonEditText = findViewById(R.id.reasonEditText)
        
        findViewById<android.widget.TextView>(R.id.doctorNameText).text = doctorName
        findViewById<android.widget.TextView>(R.id.doctorSpecialtyText).text = doctorSpecialty
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }
        
        bookButton.setOnClickListener {
            handleBooking()
        }
    }
    
    private fun populateUserData() {
        nameEditText.setText(preferenceManager.getUserName())
        emailEditText.setText(preferenceManager.getUserEmail())
        phoneEditText.setText("+1 (555) 123-4567")
    }
    
    private fun handleBooking() {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val reason = reasonEditText.text.toString()
        
        if (name.isBlank() || email.isBlank() || phone.isBlank() || reason.isBlank()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        Toast.makeText(this, "Booking appointment...", Toast.LENGTH_SHORT).show()
        
        val bookingRequest = BookingRequest(
            doctorId = doctorId,
            patientName = name,
            patientEmail = email,
            appointmentDate = "2025-01-20",
            appointmentTime = "10:00 AM",
            reason = reason
        )
        
        val userId = firebaseRepository.getCurrentUser()?.uid ?: "guest"
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                Log.d(TAG, "🔄 Submitting booking via REST API...")
                val result = dataRepository.createBooking(bookingRequest, userId)
                
                result.onSuccess { bookingResponse ->
                    Log.d(TAG, "✅ Booking created successfully: ${bookingResponse.id}")
                    Toast.makeText(this@BookingActivity, "Booking confirmed!", Toast.LENGTH_SHORT).show()
                    
                    val intent = Intent(this@BookingActivity, BookingConfirmationActivity::class.java)
                    intent.putExtra("BOOKING_ID", bookingResponse.id)
                    intent.putExtra("DOCTOR_NAME", doctorName)
                    intent.putExtra("DOCTOR_SPECIALTY", doctorSpecialty)
                    intent.putExtra("APPOINTMENT_DATE", bookingRequest.appointmentDate)
                    intent.putExtra("APPOINTMENT_TIME", bookingRequest.appointmentTime)
                    intent.putExtra("PATIENT_NAME", name)
                    intent.putExtra("PATIENT_EMAIL", email)
                    startActivity(intent)
                    
                    finish()
                }.onFailure { exception ->
                    Log.e(TAG, "❌ Booking failed: ${exception.message}")
                    Toast.makeText(this@BookingActivity, "Booking failed. Please try again.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error creating booking", e)
                Toast.makeText(this@BookingActivity, "Error creating booking. Please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
