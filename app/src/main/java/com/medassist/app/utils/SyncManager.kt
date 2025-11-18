package com.medassist.app.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.medassist.app.data.repository.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Sync Manager for offline data synchronization
 * Handles automatic background sync when network becomes available
 */
class SyncManager(private val context: Context) {

    private val dataRepository = DataRepository(context)
    private val networkUtils = NetworkUtils

    companion object {
        private const val TAG = "SyncManager"
    }

    /**
     * Performs a manual sync of articles
     * Can be called from UI (e.g., pull-to-refresh or sync button)
     */
    fun syncArticles(onComplete: (Boolean, String) -> Unit = { _, _ -> }) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🔄 Manual sync initiated...")
                
                val result = dataRepository.getArticles()
                result.onSuccess { articles ->
                    Log.d(TAG, "✅ Sync successful: ${articles.size} articles")
                    CoroutineScope(Dispatchers.Main).launch {
                        onComplete(true, "Synced ${articles.size} articles")
                        Toast.makeText(context, "Sync complete: ${articles.size} articles", Toast.LENGTH_SHORT).show()
                    }
                }.onFailure { exception ->
                    Log.e(TAG, "❌ Sync failed: ${exception.message}")
                    CoroutineScope(Dispatchers.Main).launch {
                        onComplete(false, exception.message ?: "Sync failed")
                        Toast.makeText(context, "Sync failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error during sync: ${e.message}", e)
                CoroutineScope(Dispatchers.Main).launch {
                    onComplete(false, e.message ?: "Unknown error")
                }
            }
        }
    }

    /**
     * Checks if sync is needed and performs automatic sync
     * Called when network becomes available
     */
    fun autoSyncIfNeeded() {
        if (!networkUtils.isNetworkAvailable(context)) {
            Log.d(TAG, "📴 No network - skipping auto sync")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🔄 Auto-sync initiated (network available)...")
                val result = dataRepository.getArticles()
                result.onSuccess { articles ->
                    Log.d(TAG, "✅ Auto-sync successful: ${articles.size} articles")
                }.onFailure { exception ->
                    Log.w(TAG, "⚠️ Auto-sync failed: ${exception.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error during auto-sync: ${e.message}", e)
            }
        }
    }

    /**
     * Forces a full sync (ignores cache)
     */
    fun forceSync(onComplete: (Boolean, String) -> Unit = { _, _ -> }) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🔄 Force sync initiated...")
                
                // Force sync by calling getArticles which will fetch from remote
                val result = dataRepository.getArticles()
                result.onSuccess { articles ->
                    Log.d(TAG, "✅ Force sync successful: ${articles.size} articles")
                    CoroutineScope(Dispatchers.Main).launch {
                        onComplete(true, "Force sync complete: ${articles.size} articles")
                        Toast.makeText(context, "Force sync complete", Toast.LENGTH_SHORT).show()
                    }
                }.onFailure { exception ->
                    Log.e(TAG, "❌ Force sync failed: ${exception.message}")
                    CoroutineScope(Dispatchers.Main).launch {
                        onComplete(false, exception.message ?: "Force sync failed")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error during force sync: ${e.message}", e)
                CoroutineScope(Dispatchers.Main).launch {
                    onComplete(false, e.message ?: "Unknown error")
                }
            }
        }
    }
}

