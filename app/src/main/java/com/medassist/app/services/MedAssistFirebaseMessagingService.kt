package com.medassist.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.medassist.app.MainActivity
import com.medassist.app.R

/**
 * Firebase Cloud Messaging Service for MedAssist
 * Handles push notifications for appointment reminders and updates
 */
class MedAssistFirebaseMessagingService : FirebaseMessagingService() {
    
    companion object {
        private const val TAG = "MedAssistFCMService"
        private const val CHANNEL_ID = "medassist_notifications"
        private const val NOTIFICATION_ID = 1001
    }
    
    /**
     * Called when a push notification is received
     * Handles both data payload and notification payload
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "📨 FCM Message received from: ${remoteMessage.from}")
        
        // Check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "📦 Message data payload: ${remoteMessage.data}")
            
            val title = remoteMessage.data["title"] ?: "MedAssist Notification"
            val body = remoteMessage.data["body"] ?: "You have a new notification"
            val type = remoteMessage.data["type"] ?: "general"
            
            Log.d(TAG, "✅ Displaying notification: $title - $body (type: $type)")
            showNotification(title, body, type)
        }
        
        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "📢 Message Notification Body: ${it.body}")
            showNotification(
                it.title ?: "MedAssist",
                it.body ?: "You have a new notification",
                "notification"
            )
        }
    }
    
    /**
     * Called when FCM registration token is refreshed
     * Saves token to Firestore for notification targeting
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "🔄 FCM Token refreshed: $token")
        
        // Save token to Firestore
        saveTokenToFirestore(token)
        
        // Send token to server if needed
        sendTokenToServer(token)
    }
    
    /**
     * Displays a notification to the user
     * 
     * @param title Notification title
     * @param body Notification body text
     * @param type Notification type (e.g., "booking", "article", "general")
     */
    private fun showNotification(title: String, body: String, type: String) {
        createNotificationChannel()
        
        Log.d(TAG, "🔔 Creating notification: $title")
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", type)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_article_placeholder) // Use your app icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
        
        Log.d(TAG, "✅ Notification displayed: $title")
    }
    
    /**
     * Creates notification channel for Android O and above
     * Required for displaying notifications on Android 8.0+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MedAssist Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for MedAssist app including appointment reminders"
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "✅ Notification channel created: $CHANNEL_ID")
        }
    }
    
    /**
     * Saves FCM token to Firestore for notification targeting
     */
    private fun saveTokenToFirestore(token: String) {
        try {
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            
            if (currentUser != null) {
                val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val tokenData = hashMapOf(
                    "fcmToken" to token,
                    "updatedAt" to System.currentTimeMillis(),
                    "deviceId" to android.provider.Settings.Secure.getString(
                        applicationContext.contentResolver,
                        android.provider.Settings.Secure.ANDROID_ID
                    )
                )
                
                firestore.collection("users")
                    .document(currentUser.uid)
                    .update(tokenData as Map<String, Any>)
                    .addOnSuccessListener {
                        Log.d(TAG, "✅ FCM token saved to Firestore for user: ${currentUser.uid}")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "❌ Failed to save FCM token to Firestore", e)
                    }
            } else {
                Log.w(TAG, "⚠️ No authenticated user, cannot save FCM token")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error saving FCM token to Firestore", e)
        }
    }
    
    /**
     * Sends token to backend server (if you have one)
     * This would typically send the token to your REST API server
     * so it can send notifications to this device
     */
    private fun sendTokenToServer(token: String) {
        // TODO: Implement server token registration via REST API
        // Example: POST /users/{userId}/fcm-token
        Log.d(TAG, "📤 Token ready to be sent to server: $token")
    }
}
