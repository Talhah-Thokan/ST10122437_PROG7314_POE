package com.medassist.app.data.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * Firebase Authentication Manager
 * Handles Google Sign-In flow and authentication state
 */
class FirebaseAuthManager(private val context: Context) {
    
    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseRepository = FirebaseRepository()
    
    companion object {
        private const val TAG = "FirebaseAuthManager"
        private const val RC_SIGN_IN = 1001
    }
    
    init {
        setupGoogleSignIn()
    }
    
    private fun setupGoogleSignIn() {
        try {
            // Try to get the web client ID from string resources (auto-generated from google-services.json)
            val webClientId = try {
                context.getString(com.medassist.app.R.string.default_web_client_id)
            } catch (e: Exception) {
                // Fallback: Read directly from google-services.json
                Log.w(TAG, "default_web_client_id not found in strings, trying to read from google-services.json")
                getWebClientIdFromJson()
            }
            
            if (webClientId.isNotEmpty()) {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build()
                
                googleSignInClient = GoogleSignIn.getClient(context, gso)
                Log.d(TAG, "✅ Google Sign-In configured successfully with web client ID")
            } else {
                throw Exception("Web client ID not found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error setting up Google Sign-In: ${e.message}", e)
            // Fallback: Create basic client without ID token (won't work with Firebase, but won't crash)
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(context, gso)
            Log.w(TAG, "⚠️ Using fallback Google Sign-In configuration (Firebase auth may not work)")
        }
    }
    
    /**
     * Reads web client ID directly from google-services.json file
     * This is a fallback if the string resource isn't generated
     * Note: This uses the hardcoded value from the actual google-services.json file
     */
    private fun getWebClientIdFromJson(): String {
        // Fallback: Use the web client ID from google-services.json
        // This is the client_id with client_type 3 from your google-services.json
        // For your project: 564965961341-vrudsta9nvshi9b4k5lcpgfvof89b0r3.apps.googleusercontent.com
        return try {
            // Try to read from resources first (if processed by Google Services plugin)
            val resources = context.resources
            val resourceId = resources.getIdentifier("default_web_client_id", "string", context.packageName)
            if (resourceId != 0) {
                val clientId = resources.getString(resourceId)
                Log.d(TAG, "✅ Found web client ID from resources")
                return clientId
            }
            
            // Hardcoded fallback from your google-services.json
            // This is the web client ID (client_type 3) from your Firebase project
            val fallbackClientId = "564965961341-vrudsta9nvshi9b4k5lcpgfvof89b0r3.apps.googleusercontent.com"
            Log.d(TAG, "⚠️ Using hardcoded web client ID as fallback")
            fallbackClientId
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to get web client ID: ${e.message}", e)
            // Last resort: hardcoded value from your google-services.json
            "564965961341-vrudsta9nvshi9b4k5lcpgfvof89b0r3.apps.googleusercontent.com"
        }
    }
    
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
    
    suspend fun handleSignInResult(data: Intent?): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "🔄 Processing Google Sign-In result...")
            
            if (data == null) {
                Log.e(TAG, "❌ Sign-In result data is null")
                return Result.failure(Exception("Sign-in failed: No data returned"))
            }
            
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            
            if (account != null) {
                Log.d(TAG, "✅ Google account retrieved: ${account.email}")
                firebaseSignInWithGoogle(account)
            } else {
                Log.e(TAG, "❌ Google Sign-In failed: No account returned")
                Result.failure(Exception("Google Sign-In failed: No account returned"))
            }
        } catch (e: ApiException) {
            Log.e(TAG, "❌ Google Sign-In ApiException: statusCode=${e.statusCode}, message=${e.message}", e)
            val errorMessage = when (e.statusCode) {
                10 -> "Developer error - check Firebase configuration and SHA-1 fingerprint"
                7 -> "Network error - check internet connection"
                12501 -> "Sign-in cancelled by user"
                8 -> "Internal error - please try again"
                else -> "Sign-in failed: ${e.message} (Error code: ${e.statusCode})"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e(TAG, "❌ Unexpected error during sign-in: ${e.message}", e)
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }
    
    private suspend fun firebaseSignInWithGoogle(account: GoogleSignInAccount): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "🔄 Firebase sign-in with Google: ${account.email}")
            
            if (account.idToken == null) {
                Log.e(TAG, "❌ ID token is null - web client ID may not be configured correctly")
                return Result.failure(Exception("ID token is null. Please check Firebase configuration and ensure SHA-1 fingerprint is added to Firebase Console."))
            }
            
            Log.d(TAG, "✅ ID token received, authenticating with Firebase...")
            val result = firebaseRepository.signInWithGoogle(account.idToken!!)
            
            result.onSuccess { user ->
                Log.d(TAG, "✅ Firebase authentication successful: ${user.uid}")
                // Save user profile to Firestore
                try {
                    firebaseRepository.saveUserProfile(user)
                    Log.d(TAG, "✅ User profile saved to Firestore")
                } catch (e: Exception) {
                    Log.w(TAG, "⚠️ Failed to save user profile", e)
                    // Don't fail the sign-in if profile save fails
                }
            }.onFailure { exception ->
                Log.e(TAG, "❌ Firebase authentication failed: ${exception.message}", exception)
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase authentication error: ${e.message}", e)
            Result.failure(Exception("Firebase authentication failed: ${e.message}"))
        }
    }
    
    fun signOut() {
        try {
            // Sign out from Google
            googleSignInClient.signOut()
            
            // Sign out from Firebase
            firebaseRepository.signOut()
            
            Log.d(TAG, "User signed out successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign out", e)
        }
    }
    
    fun getCurrentUser(): FirebaseUser? = firebaseRepository.getCurrentUser()
    
    fun isUserSignedIn(): Boolean = firebaseRepository.isUserSignedIn()
    
    // Guest login simulation
    suspend fun signInAsGuest(): Result<FirebaseUser> {
        return try {
            // For guest login, we'll create a temporary anonymous user
            // In a real app, you might want to use Firebase Anonymous Auth
            Log.d(TAG, "Guest login initiated")
            
            // For now, return success with current user or null
            // This allows the app to continue without Firebase authentication
            Result.success(getCurrentUser() ?: throw Exception("Guest login not available"))
        } catch (e: Exception) {
            Log.e(TAG, "Guest login failed", e)
            Result.failure(e)
        }
    }
}
