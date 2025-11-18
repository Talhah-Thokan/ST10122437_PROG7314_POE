package com.medassist.app.data.firebase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.medassist.app.R
import com.medassist.app.ui.base.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirestoreDataSeederActivity : BaseActivity() {
    
    private lateinit var seedButton: MaterialButton
    private lateinit var progressIndicator: LinearProgressIndicator
    private val seeder = FirestoreDataSeeder()
    
    companion object {
        private const val TAG = "DataSeederActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_seeder)
        
        seedButton = findViewById(R.id.seedDataButton)
        progressIndicator = findViewById(R.id.progressIndicator)
        
        seedButton.setOnClickListener {
            seedDatabase()
        }
        
        Log.d(TAG, "DataSeederActivity created")
    }
    
    private fun seedDatabase() {
        seedButton.isEnabled = false
        progressIndicator.visibility = android.view.View.VISIBLE
        
        Toast.makeText(this, "Starting database seeding...", Toast.LENGTH_SHORT).show()
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    Log.d(TAG, "Seeding articles...")
                    seeder.seedArticles()
                    
                    Log.d(TAG, "Seeding doctors...")
                    seeder.seedDoctors()
                }
                
                progressIndicator.visibility = android.view.View.GONE
                seedButton.isEnabled = true
                
                Toast.makeText(
                    this@FirestoreDataSeederActivity,
                    "✅ Database seeded successfully!\nCheck Firebase Console to verify.",
                    Toast.LENGTH_LONG
                ).show()
                
                Log.d(TAG, "Database seeding completed successfully")
                
                seedButton.postDelayed({
                    finish()
                }, 2000)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error seeding database", e)
                progressIndicator.visibility = android.view.View.GONE
                seedButton.isEnabled = true
                
                Toast.makeText(
                    this@FirestoreDataSeederActivity,
                    "❌ Error seeding database: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
