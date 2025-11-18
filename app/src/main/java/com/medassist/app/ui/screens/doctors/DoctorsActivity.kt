package com.medassist.app.ui.screens.doctors

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.medassist.app.R

/**
 * Doctors Activity for MedAssist application
 */
class DoctorsActivity : AppCompatActivity() {
    
    private lateinit var backButton: MaterialButton
    private lateinit var doctorsRecyclerView: RecyclerView
    private lateinit var doctorsAdapter: DoctorsAdapter
    
    companion object {
        private const val TAG = "MedAssistDoctorsActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctors)
        
        Log.d(TAG, "DoctorsActivity created")
        
        setupViews()
        setupClickListeners()
        setupRecyclerView()
        
        Toast.makeText(this, "Doctor listings loaded!", Toast.LENGTH_SHORT).show()
    }
    
    private fun setupViews() {
        backButton = findViewById(R.id.backButton)
        doctorsRecyclerView = findViewById(R.id.doctorsRecyclerView)
        
        Log.d(TAG, "Views setup complete")
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener {
            Log.d(TAG, "Back button clicked")
            Toast.makeText(this, "Returning to home...", Toast.LENGTH_SHORT).show()
            finish()
        }
        
        Log.d(TAG, "Click listeners setup complete")
    }
    
    private fun setupRecyclerView() {
        val doctors = getSampleDoctors()
        doctorsAdapter = DoctorsAdapter(doctors) { doctor ->
            Toast.makeText(this, "Booking appointment with ${doctor.name}...", Toast.LENGTH_SHORT).show()
        }
        
        doctorsRecyclerView.layoutManager = LinearLayoutManager(this)
        doctorsRecyclerView.adapter = doctorsAdapter
        
        Log.d(TAG, "RecyclerView setup complete with ${doctors.size} doctors")
    }
    
    private fun getSampleDoctors(): List<Doctor> {
        return listOf(
            Doctor(
                "Dr. Sarah Johnson",
                "Cardiologist",
                "4.8",
                "2.1 km away",
                "15+ years experience",
                "$150 per visit"
            ),
            Doctor(
                "Dr. Michael Chen",
                "General Practitioner",
                "4.9",
                "0.8 km away",
                "12+ years experience",
                "$120 per visit"
            ),
            Doctor(
                "Dr. Emily Rodriguez",
                "Dermatologist",
                "4.7",
                "3.2 km away",
                "8+ years experience",
                "$180 per visit"
            ),
            Doctor(
                "Dr. James Wilson",
                "Orthopedist",
                "4.6",
                "1.5 km away",
                "20+ years experience",
                "$200 per visit"
            ),
            Doctor(
                "Dr. Lisa Thompson",
                "Pediatrician",
                "4.9",
                "2.8 km away",
                "10+ years experience",
                "$130 per visit"
            ),
            Doctor(
                "Dr. David Park",
                "Neurologist",
                "4.8",
                "4.1 km away",
                "18+ years experience",
                "$220 per visit"
            )
        )
    }
}

