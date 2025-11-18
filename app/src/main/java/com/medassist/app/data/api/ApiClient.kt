package com.medassist.app.data.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * REST API Client for MedAssist
 * 
 * Connects to localhost Ktor server:
 * - Android Emulator: http://10.0.2.2:8080
 * - Physical Device: http://YOUR_COMPUTER_IP:8080
 * 
 * Falls back to Firebase if REST API is unavailable
 */
object ApiClient {
    
    private const val TAG = "ApiClient"
    
    // Base URL for Android Emulator (10.0.2.2 maps to host machine's localhost)
    // For physical device, replace with your computer's IP address
    private const val BASE_URL_EMULATOR = "http://10.0.2.2:8080/"
    private const val BASE_URL_PHYSICAL = "http://192.168.1.100:8080/" // Update with your IP
    
    // Use emulator URL by default (change to BASE_URL_PHYSICAL for physical device)
    private const val BASE_URL = BASE_URL_EMULATOR
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: MedAssistApi = retrofit.create(MedAssistApi::class.java)
    
    init {
        Log.d(TAG, "REST API Client initialized with base URL: $BASE_URL")
    }
    
    /**
     * Check if REST API is available
     * Returns true if server is reachable, false otherwise
     */
    suspend fun isApiAvailable(): Boolean {
        return try {
            // Simple connectivity check - in production, use a health endpoint
            val response = okHttpClient.newCall(
                okhttp3.Request.Builder()
                    .url("${BASE_URL}articles")
                    .get()
                    .build()
            ).execute()
            
            val isAvailable = response.isSuccessful
            Log.d(TAG, "REST API availability check: $isAvailable")
            isAvailable
        } catch (e: Exception) {
            Log.w(TAG, "REST API not available, will use Firebase fallback: ${e.message}")
            false
        }
    }
}

