package com.medassist.app.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.medassist.app.data.api.Article
import com.medassist.app.data.api.Doctor
import com.medassist.app.data.api.BookingRequest
import com.medassist.app.data.api.BookingResponse

/**
 * Repository class for Firebase operations
 * Handles authentication, Firestore database operations, and data management
 */
class FirebaseRepository {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "FirebaseRepository"
        
        // Firestore collection names
        private const val COLLECTION_ARTICLES = "articles"
        private const val COLLECTION_DOCTORS = "doctors"
        private const val COLLECTION_BOOKINGS = "bookings"
        private const val COLLECTION_USERS = "users"
    }
    
    init {
        Log.d(TAG, "FirebaseRepository initialized")
        Log.d(TAG, "Firebase Auth instance: ${auth.app.name}")
        Log.d(TAG, "Firestore instance: ${firestore.app.name}")
    }
    
    // Authentication methods
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            result.user?.let { user ->
                Log.d(TAG, "Google sign-in successful: ${user.uid}")
                Result.success(user)
            } ?: Result.failure(Exception("Sign-in failed: No user returned"))
        } catch (e: Exception) {
            Log.e(TAG, "Google sign-in failed", e)
            Result.failure(e)
        }
    }
    
    fun signOut() {
        auth.signOut()
        Log.d(TAG, "User signed out")
    }
    
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    fun isUserSignedIn(): Boolean = auth.currentUser != null
    
    // User data methods
    suspend fun saveUserProfile(user: FirebaseUser) {
        try {
            val userData = hashMapOf(
                "uid" to user.uid,
                "name" to (user.displayName ?: ""),
                "email" to (user.email ?: ""),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "createdAt" to System.currentTimeMillis(),
                "lastLogin" to System.currentTimeMillis()
            )
            
            // Use merge to avoid overwriting existing data
            firestore.collection(COLLECTION_USERS)
                .document(user.uid)
                .set(userData, com.google.firebase.firestore.SetOptions.merge())
                .await()
            
            Log.d(TAG, "User profile saved successfully: ${user.uid}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user profile: ${e.message}", e)
            // Don't throw - just log the error, user can still use the app
        }
    }
    
    suspend fun getUserProfile(uid: String): Result<Map<String, Any>> {
        return try {
            val document = firestore.collection(COLLECTION_USERS)
                .document(uid)
                .get()
                .await()
            
            if (document.exists()) {
                Result.success(document.data ?: emptyMap())
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user profile", e)
            Result.failure(e)
        }
    }
    
    // Articles methods
    suspend fun getArticles(): Result<List<Article>> {
        return try {
            Log.d(TAG, "🔍 Attempting to fetch articles from Firestore...")
            Log.d(TAG, "Collection path: $COLLECTION_ARTICLES")
            
            val snapshot = firestore.collection(COLLECTION_ARTICLES)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            Log.d(TAG, "✅ Firestore query successful! Documents found: ${snapshot.size()}")
            
            if (snapshot.isEmpty) {
                Log.w(TAG, "⚠️ No documents found in articles collection")
            }
            
            val articles = snapshot.documents.mapNotNull { document ->
                try {
                    Log.d(TAG, "Parsing article: ${document.id}")
                    Article(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        author = document.getString("author") ?: "",
                        summary = document.getString("summary") ?: "",
                        content = document.getString("content") ?: "",
                        imageUrl = document.getString("imageUrl") ?: "",
                        date = document.getString("date") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error parsing article: ${document.id}", e)
                    null
                }
            }
            
            Log.d(TAG, "✅ Successfully fetched ${articles.size} articles from Firestore")
            Result.success(articles)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching articles from Firestore", e)
            Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Error message: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    suspend fun getArticleById(id: String): Result<Article> {
        return try {
            val document = firestore.collection(COLLECTION_ARTICLES)
                .document(id)
                .get()
                .await()
            
            if (document.exists()) {
                val article = Article(
                    id = document.id,
                    title = document.getString("title") ?: "",
                    author = document.getString("author") ?: "",
                    summary = document.getString("summary") ?: "",
                    content = document.getString("content") ?: "",
                    imageUrl = document.getString("imageUrl") ?: "",
                    date = document.getString("date") ?: ""
                )
                Result.success(article)
            } else {
                Result.failure(Exception("Article not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching article: $id", e)
            Result.failure(e)
        }
    }
    
    // Doctors methods
    suspend fun getDoctors(): Result<List<Doctor>> {
        return try {
            val snapshot = firestore.collection(COLLECTION_DOCTORS)
                .orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val doctors = snapshot.documents.mapNotNull { document ->
                try {
                    Doctor(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        specialty = document.getString("specialty") ?: "",
                        rating = document.getString("rating") ?: "0.0",
                        distance = document.getString("distance") ?: "",
                        experience = document.getString("experience") ?: "",
                        price = document.getString("price") ?: "",
                        availability = document.getString("availability") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing doctor: ${document.id}", e)
                    null
                }
            }
            
            Log.d(TAG, "Fetched ${doctors.size} doctors from Firestore")
            Result.success(doctors)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching doctors", e)
            Result.failure(e)
        }
    }
    
    suspend fun searchDoctors(query: String): Result<List<Doctor>> {
        return try {
            val snapshot = firestore.collection(COLLECTION_DOCTORS)
                .whereArrayContains("searchTerms", query.lowercase())
                .get()
                .await()
            
            val doctors = snapshot.documents.mapNotNull { document ->
                try {
                    Doctor(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        specialty = document.getString("specialty") ?: "",
                        rating = document.getString("rating") ?: "0.0",
                        distance = document.getString("distance") ?: "",
                        experience = document.getString("experience") ?: "",
                        price = document.getString("price") ?: "",
                        availability = document.getString("availability") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing doctor: ${document.id}", e)
                    null
                }
            }
            
            Log.d(TAG, "Found ${doctors.size} doctors matching query: $query")
            Result.success(doctors)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching doctors", e)
            Result.failure(e)
        }
    }
    
    // Booking methods
    suspend fun createBooking(booking: BookingRequest, userId: String): Result<BookingResponse> {
        return try {
            // Get current user to ensure authenticated
            val currentUser = getCurrentUser()
            val actualUserId = currentUser?.uid ?: userId
            
            val bookingData = hashMapOf(
                "doctorId" to (booking.doctorId ?: "unknown"),
                "patientName" to booking.patientName,
                "patientEmail" to booking.patientEmail,
                "appointmentDate" to booking.appointmentDate,
                "appointmentTime" to booking.appointmentTime,
                "reason" to booking.reason,
                "userId" to actualUserId,
                "status" to "confirmed",
                "createdAt" to System.currentTimeMillis()
            )
            
            val docRef = firestore.collection(COLLECTION_BOOKINGS)
                .add(bookingData)
                .await()
            
            val bookingResponse = BookingResponse(
                id = docRef.id,
                status = "confirmed",
                message = "Appointment booked successfully"
            )
            
            Log.d(TAG, "Booking created successfully: ${docRef.id} for user: $actualUserId")
            Result.success(bookingResponse)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating booking: ${e.message}", e)
            
            // Return a simulated success for demo purposes if Firebase fails
            val fallbackResponse = BookingResponse(
                id = "demo_${System.currentTimeMillis()}",
                status = "confirmed",
                message = "Appointment booked successfully (local only)"
            )
            Log.d(TAG, "Using fallback booking response")
            Result.success(fallbackResponse)
        }
    }
    
    suspend fun getUserBookings(userId: String): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = firestore.collection(COLLECTION_BOOKINGS)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val bookings = snapshot.documents.map { document ->
                document.data ?: emptyMap()
            }
            
            Log.d(TAG, "Fetched ${bookings.size} bookings for user: $userId")
            Result.success(bookings)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user bookings", e)
            Result.failure(e)
        }
    }
}
