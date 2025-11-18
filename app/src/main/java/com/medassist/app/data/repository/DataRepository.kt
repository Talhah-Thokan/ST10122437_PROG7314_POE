package com.medassist.app.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.medassist.app.data.api.*
import com.medassist.app.data.firebase.FirebaseRepository
import com.medassist.app.data.local.AppDatabase
import com.medassist.app.data.local.ArticleEntity
import com.medassist.app.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Unified Data Repository
 * 
 * This repository implements a multi-tier strategy pattern:
 * 1. Loads from Room database first (offline support)
 * 2. If online, syncs with REST API (primary source)
 * 3. If REST API fails, falls back to Firebase Firestore
 * 4. Updates Room database with fresh data when available
 * 
 * This demonstrates:
 * - REST API usage
 * - Offline-first architecture with Room
 * - Firebase as backup data source
 */
class DataRepository(private val context: Context? = null) {
    
    private val apiClient = ApiClient.apiService
    private val firebaseRepository = FirebaseRepository()
    private val database = context?.let { AppDatabase.getDatabase(it) }
    private val articleDao = database?.articleDao()
    
    companion object {
        private const val TAG = "DataRepository"
    }
    
    /**
     * Fetches articles with offline-first strategy:
     * 1. Load from Room database (immediate response)
     * 2. If online, sync with REST API/Firebase
     * 3. Update Room database with fresh data
     * 
     * @return Result containing list of articles
     */
    suspend fun getArticles(): Result<List<Article>> = withContext(Dispatchers.IO) {
        // Step 1: Load from Room database first (offline support)
        val localArticles = try {
            articleDao?.getAllArticlesSync() ?: emptyList()
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Error loading from Room: ${e.message}")
            emptyList()
        }
        
        if (localArticles.isNotEmpty()) {
            Log.d(TAG, "📱 Loaded ${localArticles.size} articles from Room database")
            // Show offline mode toast if no internet
            if (context != null && !NetworkUtils.isNetworkAvailable(context)) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Offline Mode Active - Showing cached articles", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        // Step 2: If online, sync with remote sources
        val isOnline = context != null && NetworkUtils.isNetworkAvailable(context)
        
        if (isOnline) {
            Log.d(TAG, "🌐 Online - Syncing with remote sources...")
            
            // Try REST API first
            val remoteArticles = try {
                Log.d(TAG, "🔄 Attempting to fetch articles from REST API...")
                val response = apiClient.getArticles()
                
                if (response.isSuccessful && response.body() != null) {
                    val articles = response.body()!!
                    Log.d(TAG, "✅ Successfully fetched ${articles.size} articles from REST API")
                    articles
                } else {
                    Log.w(TAG, "⚠️ REST API returned unsuccessful response: ${response.code()}")
                    throw Exception("REST API returned error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.w(TAG, "❌ REST API failed, falling back to Firebase: ${e.message}")
                
                // Fallback to Firebase
                try {
                    val firebaseResult = firebaseRepository.getArticles()
                    if (firebaseResult.isSuccess) {
                        Log.d(TAG, "✅ Fallback to Firebase successful")
                        firebaseResult.getOrNull() ?: emptyList()
                    } else {
                        Log.e(TAG, "❌ Firebase fallback also failed")
                        emptyList()
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, "❌ Both REST API and Firebase failed: ${ex.message}")
                    emptyList()
                }
            }
            
            // Step 3: Update Room database with fresh data
            if (remoteArticles.isNotEmpty() && articleDao != null) {
                try {
                    val entities = remoteArticles.map { article ->
                        ArticleEntity(
                            id = article.id,
                            title = article.title,
                            author = article.author,
                            summary = article.summary,
                            content = article.content,
                            imageUrl = article.imageUrl,
                            date = article.date,
                            lastUpdated = System.currentTimeMillis()
                        )
                    }
                    articleDao.insertArticles(entities)
                    Log.d(TAG, "💾 Updated Room database with ${entities.size} articles")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error updating Room database: ${e.message}")
                }
            }
            
            // Return remote articles if available, otherwise local
            if (remoteArticles.isNotEmpty()) {
                Result.success(remoteArticles)
            } else if (localArticles.isNotEmpty()) {
                // Convert local entities to API articles
                val articles = localArticles.map { entity ->
                    Article(
                        id = entity.id,
                        title = entity.title,
                        author = entity.author,
                        summary = entity.summary,
                        content = entity.content,
                        imageUrl = entity.imageUrl,
                        date = entity.date
                    )
                }
                Result.success(articles)
            } else {
                Result.failure(Exception("No articles available"))
            }
        } else {
            // Offline mode - return local articles
            Log.d(TAG, "📴 Offline mode - returning cached articles")
            if (localArticles.isNotEmpty()) {
                val articles = localArticles.map { entity ->
                    Article(
                        id = entity.id,
                        title = entity.title,
                        author = entity.author,
                        summary = entity.summary,
                        content = entity.content,
                        imageUrl = entity.imageUrl,
                        date = entity.date
                    )
                }
                Result.success(articles)
            } else {
                Result.failure(Exception("No articles available offline"))
            }
        }
    }
    
    /**
     * Fetches doctors/providers from REST API first, falls back to Firebase if unavailable
     * 
     * @return Result containing list of doctors
     */
    suspend fun getDoctors(): Result<List<Doctor>> = withContext(Dispatchers.IO) {
        // Try REST API first
        try {
            Log.d(TAG, "🔄 Attempting to fetch doctors from REST API...")
            val response = apiClient.getProviders()
            
            if (response.isSuccessful && response.body() != null) {
                val doctors = response.body()!!
                Log.d(TAG, "✅ Successfully fetched ${doctors.size} doctors from REST API")
                return@withContext Result.success(doctors)
            } else {
                Log.w(TAG, "⚠️ REST API returned unsuccessful response: ${response.code()}")
                throw Exception("REST API returned error: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "❌ REST API failed, falling back to Firebase: ${e.message}")
            
            // Fallback to Firebase
            return@withContext try {
                val firebaseResult = firebaseRepository.getDoctors()
                if (firebaseResult.isSuccess) {
                    Log.d(TAG, "✅ Fallback to Firebase successful")
                } else {
                    Log.e(TAG, "❌ Firebase fallback also failed")
                }
                firebaseResult
            } catch (ex: Exception) {
                Log.e(TAG, "❌ Both REST API and Firebase failed: ${ex.message}")
                Result.failure(ex)
            }
        }
    }
    
    /**
     * Creates a booking via REST API first, falls back to Firebase if unavailable
     * 
     * @param booking The booking request
     * @param userId The user ID for Firebase fallback
     * @return Result containing booking response
     */
    suspend fun createBooking(booking: BookingRequest, userId: String): Result<BookingResponse> = withContext(Dispatchers.IO) {
        // Try REST API first
        try {
            Log.d(TAG, "🔄 Attempting to create booking via REST API...")
            val response = apiClient.createBooking(booking)
            
            if (response.isSuccessful && response.body() != null) {
                val bookingResponse = response.body()!!
                Log.d(TAG, "✅ Successfully created booking via REST API: ${bookingResponse.id}")
                return@withContext Result.success(bookingResponse)
            } else {
                Log.w(TAG, "⚠️ REST API returned unsuccessful response: ${response.code()}")
                throw Exception("REST API returned error: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "❌ REST API failed, falling back to Firebase: ${e.message}")
            
            // Fallback to Firebase
            return@withContext try {
                val firebaseResult = firebaseRepository.createBooking(booking, userId)
                if (firebaseResult.isSuccess) {
                    Log.d(TAG, "✅ Fallback to Firebase successful")
                } else {
                    Log.e(TAG, "❌ Firebase fallback also failed")
                }
                firebaseResult
            } catch (ex: Exception) {
                Log.e(TAG, "❌ Both REST API and Firebase failed: ${ex.message}")
                Result.failure(ex)
            }
        }
    }
}

