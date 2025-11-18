package com.medassist.app.ui.screens.articles

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.medassist.app.R
import com.medassist.app.ui.base.BaseActivity

class ArticleDetailActivity : BaseActivity() {
    
    private lateinit var backButton: MaterialButton
    private lateinit var shareButton: MaterialButton
    
    companion object {
        private const val TAG = "ArticleDetailActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)
        
        setupViews()
        setupClickListeners()
        displayArticleContent()
        
        Log.d(TAG, "ArticleDetailActivity created")
    }
    
    private fun setupViews() {
        backButton = findViewById(R.id.backButton)
        shareButton = findViewById(R.id.shareButton)
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }
        
        shareButton.setOnClickListener {
            shareArticle()
        }
    }
    
    private fun displayArticleContent() {
        val title = intent.getStringExtra("ARTICLE_TITLE") ?: ""
        val author = intent.getStringExtra("ARTICLE_AUTHOR") ?: ""
        val content = intent.getStringExtra("ARTICLE_CONTENT") ?: ""
        val imageUrl = intent.getStringExtra("ARTICLE_IMAGE") ?: ""
        
        findViewById<android.widget.TextView>(R.id.articleTitleText).text = title
        findViewById<android.widget.TextView>(R.id.articleAuthorText).text = "By $author"
        findViewById<android.widget.TextView>(R.id.articleContentText).text = content
        
        findViewById<android.widget.ImageView>(R.id.articleImageView).setImageResource(R.drawable.ic_article_placeholder)
        
        Log.d(TAG, "Displaying article: $title")
    }
    
    private fun shareArticle() {
        val title = intent.getStringExtra("ARTICLE_TITLE") ?: ""
        val author = intent.getStringExtra("ARTICLE_AUTHOR") ?: ""
        
        val shareText = "Check out this health article: \"$title\" by $author on MedAssist!"
        
        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        
        startActivity(android.content.Intent.createChooser(shareIntent, "Share article"))
    }
}
