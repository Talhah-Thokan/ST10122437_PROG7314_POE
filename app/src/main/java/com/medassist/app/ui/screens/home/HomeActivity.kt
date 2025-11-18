package com.medassist.app.ui.screens.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.medassist.app.R
import com.medassist.app.ui.screens.articles.ArticlesActivity
import com.medassist.app.ui.screens.doctors.DoctorsActivity
import com.medassist.app.ui.screens.profile.ProfileActivity

/**
 * Home Activity for MedAssist application
 */
class HomeActivity : AppCompatActivity() {
    
    private lateinit var logoutButton: MaterialButton
    private lateinit var profileButton: MaterialButton
    private lateinit var articlesButton: MaterialButton
    private lateinit var doctorsButton: MaterialButton
    
    companion object {
        private const val TAG = "MedAssistHomeActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        
        Log.d(TAG, "HomeActivity created")
        
        setupViews()
        setupClickListeners()
        
        // Show welcome message
        val userName = intent.getStringExtra("USER_NAME") ?: "Guest"
        Toast.makeText(this, "Welcome to MedAssist, $userName!", Toast.LENGTH_LONG).show()
    }
    
    private fun setupViews() {
        logoutButton = findViewById(R.id.logoutButton)
        profileButton = findViewById(R.id.profileButton)
        articlesButton = findViewById(R.id.articlesButton)
        doctorsButton = findViewById(R.id.doctorsButton)
        
        Log.d(TAG, "Views setup complete")
    }
    
    private fun setupClickListeners() {
        logoutButton.setOnClickListener {
            Log.d(TAG, "Logout button clicked")
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
            // Go back to login screen
            finish()
        }
        
        profileButton.setOnClickListener {
            Log.d(TAG, "Profile button clicked")
            val intent = Intent(this, ProfileActivity::class.java)
            val userName = getIntent().getStringExtra("USER_NAME") ?: "Guest"
            intent.putExtra("USER_NAME", userName)
            startActivity(intent)
        }
        
        articlesButton.setOnClickListener {
            Log.d(TAG, "Articles button clicked")
            val intent = Intent(this, ArticlesActivity::class.java)
            startActivity(intent)
        }
        
        doctorsButton.setOnClickListener {
            Log.d(TAG, "Doctors button clicked")
            val intent = Intent(this, DoctorsActivity::class.java)
            startActivity(intent)
        }
        
        Log.d(TAG, "Click listeners setup complete")
    }
}
