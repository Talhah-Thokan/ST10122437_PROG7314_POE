package com.medassist.app.ui.screens.booking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.medassist.app.MainActivity
import com.medassist.app.R
import com.medassist.app.ui.base.BaseActivity

class BookingConfirmationActivity : BaseActivity() {
    
    private lateinit var homeButton: MaterialButton
    private lateinit var newBookingButton: MaterialButton
    
    companion object {
        private const val TAG = "BookingConfirmationActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmation)
        
        setupViews()
        setupClickListeners()
        displayBookingDetails()
        showNotification()
        
        Log.d(TAG, "BookingConfirmationActivity created")
    }
    
    private fun setupViews() {
        homeButton = findViewById(R.id.homeButton)
        newBookingButton = findViewById(R.id.newBookingButton)
    }
    
    private fun setupClickListeners() {
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        
        newBookingButton.setOnClickListener {
            finish()
        }
    }
    
    private fun displayBookingDetails() {
        val bookingId = intent.getStringExtra("BOOKING_ID") ?: ""
        val doctorName = intent.getStringExtra("DOCTOR_NAME") ?: ""
        val doctorSpecialty = intent.getStringExtra("DOCTOR_SPECIALTY") ?: ""
        val appointmentDate = intent.getStringExtra("APPOINTMENT_DATE") ?: ""
        val appointmentTime = intent.getStringExtra("APPOINTMENT_TIME") ?: ""
        val patientName = intent.getStringExtra("PATIENT_NAME") ?: ""
        
        findViewById<android.widget.TextView>(R.id.bookingIdText).text = "Booking ID: $bookingId"
        findViewById<android.widget.TextView>(R.id.doctorNameText).text = doctorName
        findViewById<android.widget.TextView>(R.id.doctorSpecialtyText).text = doctorSpecialty
        findViewById<android.widget.TextView>(R.id.appointmentDateText).text = appointmentDate
        findViewById<android.widget.TextView>(R.id.appointmentTimeText).text = appointmentTime
        findViewById<android.widget.TextView>(R.id.patientNameText).text = patientName
        
        Log.d(TAG, "Booking confirmed: $bookingId for $patientName with $doctorName")
    }
    
    private fun showNotification() {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "✅ Appointment booked successfully! Confirmation sent to your email.",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("OK") {
            snackbar.dismiss()
        }
        snackbar.show()
        
        Toast.makeText(
            this, 
            "Appointment confirmed! You will receive a reminder 24 hours before your visit.", 
            Toast.LENGTH_LONG
        ).show()
    }
}
