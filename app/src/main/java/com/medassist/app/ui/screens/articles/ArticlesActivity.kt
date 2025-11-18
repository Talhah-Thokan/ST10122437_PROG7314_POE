package com.medassist.app.ui.screens.articles

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.medassist.app.R

/**
 * Articles Activity for MedAssist application
 */
class ArticlesActivity : AppCompatActivity() {
    
    private lateinit var backButton: MaterialButton
    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var articlesAdapter: ArticlesAdapter
    
    companion object {
        private const val TAG = "MedAssistArticlesActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articles)
        
        Log.d(TAG, "ArticlesActivity created")
        
        setupViews()
        setupClickListeners()
        setupRecyclerView()
        
        Toast.makeText(this, "Health Articles loaded!", Toast.LENGTH_SHORT).show()
    }
    
    private fun setupViews() {
        backButton = findViewById(R.id.backButton)
        articlesRecyclerView = findViewById(R.id.articlesRecyclerView)
        
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
        val articles = getSampleArticles()
        articlesAdapter = ArticlesAdapter(articles) { article ->
            Toast.makeText(this, "Opening: ${article.title}", Toast.LENGTH_SHORT).show()
        }
        
        articlesRecyclerView.layoutManager = LinearLayoutManager(this)
        articlesRecyclerView.adapter = articlesAdapter
        
        Log.d(TAG, "RecyclerView setup complete with ${articles.size} articles")
    }
    
    private fun getSampleArticles(): List<Article> {
        return listOf(
            Article(
                "5 Tips for Better Sleep",
                "Dr. Sarah Johnson",
                "Learn essential techniques to improve your sleep quality and overall health.",
                "2025-01-15"
            ),
            Article(
                "Understanding Heart Health",
                "Dr. Michael Chen",
                "Everything you need to know about maintaining a healthy heart and preventing cardiovascular diseases.",
                "2025-01-14"
            ),
            Article(
                "Mental Health and Wellness",
                "Dr. Emily Rodriguez",
                "Practical strategies for managing stress and maintaining good mental health.",
                "2025-01-13"
            ),
            Article(
                "Nutrition for Active Living",
                "Dr. James Wilson",
                "Discover the best foods to fuel your active lifestyle and maintain optimal health.",
                "2025-01-12"
            ),
            Article(
                "Preventing Common Cold",
                "Dr. Lisa Thompson",
                "Simple steps to boost your immunity and avoid seasonal illnesses.",
                "2025-01-11"
            )
        )
    }
}

