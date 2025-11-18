package com.medassist.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medassist.app.R
import com.medassist.app.data.repository.DataRepository
import com.medassist.app.data.api.Article as ApiArticle
import com.medassist.app.ui.screens.articles.ArticleDetailActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Articles Fragment - Displays health articles with modern card layout
 * 
 * This fragment uses DataRepository which attempts to fetch from REST API first,
 * then falls back to Firebase if the REST API is unavailable.
 */
class ArticlesFragment : Fragment() {
    
    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var articlesAdapter: ArticlesAdapter
    private lateinit var dataRepository: DataRepository
    
    companion object {
        private const val TAG = "ArticlesFragment"
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_articles, container, false)
        
        dataRepository = DataRepository(requireContext())
        setupViews(view)
        loadArticles()
        
        Log.d(TAG, "ArticlesFragment created")
        
        return view
    }
    
    private fun setupViews(view: View) {
        articlesRecyclerView = view.findViewById(R.id.articlesRecyclerView)
    }
    
    /**
     * Loads articles from REST API first, falls back to Firebase if unavailable
     */
    private fun loadArticles() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                Log.d(TAG, "🔄 Loading articles...")
                val result = dataRepository.getArticles()
                result.onSuccess { articles ->
                    setupRecyclerView(articles)
                    Log.d(TAG, "✅ Successfully loaded ${articles.size} articles")
                    Toast.makeText(requireContext(), "Loaded ${articles.size} articles", Toast.LENGTH_SHORT).show()
                }.onFailure { exception ->
                    Log.e(TAG, "❌ Failed to load articles from both REST API and Firebase", exception)
                    Toast.makeText(requireContext(), "Failed to load articles. Using sample data.", Toast.LENGTH_SHORT).show()
                    // Fallback to sample articles
                    setupRecyclerView(getSampleArticles())
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading articles", e)
                Toast.makeText(requireContext(), "Error loading articles. Using sample data.", Toast.LENGTH_SHORT).show()
                // Fallback to sample articles
                setupRecyclerView(getSampleArticles())
            }
        }
    }
    
    private fun setupRecyclerView(articles: List<ApiArticle>) {
        articlesAdapter = ArticlesAdapter(articles) { article ->
            // Navigate to article detail
            val intent = Intent(requireContext(), ArticleDetailActivity::class.java)
            intent.putExtra("ARTICLE_TITLE", article.title)
            intent.putExtra("ARTICLE_AUTHOR", article.author)
            intent.putExtra("ARTICLE_CONTENT", article.content)
            intent.putExtra("ARTICLE_IMAGE", article.imageUrl)
            startActivity(intent)
        }
        
        articlesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        articlesRecyclerView.adapter = articlesAdapter
    }
    
            private fun getSampleArticles(): List<ApiArticle> {
                return listOf(
                    ApiArticle(
                        id = "1",
                        title = "Managing Diabetes in South Africa: A Comprehensive Guide",
                        author = "Dr. Thabo Ndlovu",
                        summary = "With diabetes on the rise in SA, learn how to manage your blood sugar levels and live a healthy life.",
                        content = "Diabetes is a growing concern in South Africa, affecting millions of people. This comprehensive guide covers blood sugar management, dietary recommendations tailored to South African cuisine, exercise tips, and how to access affordable medication through public healthcare. Learn about the latest diabetes management techniques available at local clinics and hospitals.",
                        imageUrl = "https://via.placeholder.com/400x250/008B8B/FFFFFF?text=MedAssist+Health",
                        date = "2025-10-05"
                    ),
                    ApiArticle(
                        id = "2",
                        title = "Winter Health Tips for South Africans",
                        author = "Dr. Zanele Khumalo",
                        summary = "Stay healthy this winter with practical tips for preventing flu, colds, and other seasonal illnesses.",
                        content = "As winter approaches in South Africa, it's important to protect yourself from seasonal illnesses. This article covers vaccination schedules, immune-boosting foods available in local markets, proper layering for cold mornings, and when to visit your local clinic. Learn about free flu vaccinations available at public health facilities.",
                        imageUrl = "https://via.placeholder.com/400x250/20B2AA/FFFFFF?text=MedAssist+Health",
                        date = "2025-10-02"
                    ),
                    ApiArticle(
                        id = "3",
                        title = "Mental Health Resources in South Africa",
                        author = "Dr. Sipho Mkhize",
                        summary = "Mental health matters. Discover free and affordable mental health resources available across South Africa.",
                        content = "Mental health is a priority, and South Africa offers various support systems. This guide covers SADAG (South African Depression and Anxiety Group), community health centers, online counseling services, and how to access mental health care through medical aid schemes. Learn about free helplines and support groups in your area.",
                        imageUrl = "https://via.placeholder.com/400x250/006666/FFFFFF?text=MedAssist+Health",
                        date = "2025-09-28"
                    ),
                    ApiArticle(
                        id = "4",
                        title = "Understanding Medical Aid Schemes in South Africa",
                        author = "Dr. Lerato Mokoena",
                        summary = "Navigate the complex world of medical aids and choose the right plan for you and your family.",
                        content = "Choosing the right medical aid in South Africa can be overwhelming. This article breaks down the differences between schemes like Discovery, Momentum, Bonitas, and Medihelp. Learn about PMBs (Prescribed Minimum Benefits), hospital plans vs comprehensive plans, and how to maximize your benefits. Includes tips for those without medical aid on accessing quality healthcare.",
                        imageUrl = "https://via.placeholder.com/400x250/008B8B/FFFFFF?text=MedAssist+Health",
                        date = "2025-09-25"
                    ),
                    ApiArticle(
                        id = "5",
                        title = "Healthy Eating on a Budget in South Africa",
                        author = "Dr. Nomvula Dlamini",
                        summary = "Nutritious meals don't have to be expensive. Learn how to eat healthy with affordable South African ingredients.",
                        content = "Eating healthy is possible even on a tight budget in South Africa. This guide covers affordable nutritious foods available at local markets, meal planning with staples like pap, beans, and seasonal vegetables, and budget-friendly protein sources. Learn how to create balanced meals for under R50 per person and discover community feeding programs and food gardens.",
                        imageUrl = "https://via.placeholder.com/400x250/20B2AA/FFFFFF?text=MedAssist+Health",
                        date = "2025-09-20"
                    )
                )
            }
}

/**
 * Data class for articles
 */
data class ApiArticle(
    val title: String,
    val author: String,
    val summary: String,
    val content: String,
    val imageUrl: String,
    val date: String
)

/**
 * Adapter for articles RecyclerView with modern card design
 */
class ArticlesAdapter(
    private val articles: List<ApiArticle>,
    private val onItemClick: (ApiArticle) -> Unit
) : RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: android.widget.TextView = view.findViewById(R.id.articleTitle)
        val authorText: android.widget.TextView = view.findViewById(R.id.articleAuthor)
        val summaryText: android.widget.TextView = view.findViewById(R.id.articleSummary)
        val dateText: android.widget.TextView = view.findViewById(R.id.articleDate)
        val imageView: android.widget.ImageView = view.findViewById(R.id.articleImage)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article_modern, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        
        holder.titleText.text = article.title
        holder.authorText.text = "By ${article.author}"
        holder.summaryText.text = article.summary
        holder.dateText.text = article.date
        
        // TODO: Load image using Glide or Picasso
        // For now, we'll use a placeholder
        holder.imageView.setImageResource(R.drawable.ic_article_placeholder)
        
        holder.itemView.setOnClickListener {
            onItemClick(article)
        }
    }
    
    override fun getItemCount(): Int = articles.size
}

