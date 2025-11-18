package com.medassist.app.ui.screens.profile

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.medassist.app.R
import com.medassist.app.ui.base.BaseActivity
import com.medassist.app.utils.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProfileActivity : BaseActivity() {

    private lateinit var backButton: MaterialButton
    private lateinit var saveProfileButton: MaterialButton
    private lateinit var uploadProfileImageButton: MaterialButton
    private lateinit var profileImageView: ImageView
    private lateinit var userNameEditText: EditText
    private lateinit var userEmailText: TextView
    private lateinit var preferenceManager: PreferenceManager

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "ProfileActivity"
        private const val PICK_IMAGE_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        preferenceManager = PreferenceManager(this)

        setupViews()
        setupClickListeners()
        loadUserProfile()

        Log.d(TAG, "ProfileActivity created")
    }

    private fun setupViews() {
        backButton = findViewById(R.id.backButton)
        saveProfileButton = findViewById(R.id.saveProfileButton)
        uploadProfileImageButton = findViewById(R.id.uploadProfileImageButton)
        profileImageView = findViewById(R.id.profileImageView)
        userNameEditText = findViewById(R.id.userNameEditText)
        userEmailText = findViewById(R.id.userEmailText)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }

        uploadProfileImageButton.setOnClickListener { openImagePicker() }

        saveProfileButton.setOnClickListener { handleSaveProfile() }
    }

    private fun loadUserProfile() {
        userNameEditText.setText(preferenceManager.getUserName())
        userEmailText.text = preferenceManager.getUserEmail()
        loadProfileImage()
    }

    private fun handleSaveProfile() {
        val newName = userNameEditText.text.toString()
        if (newName.isBlank()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        preferenceManager.setUserName(newName)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    firestore.collection("users").document(currentUser.uid)
                        .update("name", newName)
                        .await()
                    launch(Dispatchers.Main) {
                        Toast.makeText(this@ProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        showProfileUpdateNotification(newName)
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(this@ProfileActivity, "Failed to update profile: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Profile updated locally", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uploadImageToFirebaseStorage(it) }
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val currentUser = auth.currentUser ?: return
        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val filename = "profile_images/${currentUser.uid}/${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference.child(filename)
                val downloadUrl = storageRef.putFile(imageUri).await().storage.downloadUrl.await()

                firestore.collection("users").document(currentUser.uid)
                    .update(mapOf("photoUrl" to downloadUrl.toString()))
                    .await()

                launch(Dispatchers.Main) {
                    Toast.makeText(this@ProfileActivity, "Profile picture uploaded!", Toast.LENGTH_SHORT).show()
                    loadProfileImage()
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(this@ProfileActivity, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadProfileImage() {
        val currentUser = auth.currentUser ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val photoUrl = firestore.collection("users").document(currentUser.uid).get()
                    .await().getString("photoUrl")

                launch(Dispatchers.Main) {
                    Glide.with(this@ProfileActivity)
                        .load(photoUrl)
                        .placeholder(R.drawable.ic_article_placeholder)
                        .circleCrop()
                        .into(profileImageView)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile image", e)
            }
        }
    }

    private fun showProfileUpdateNotification(newName: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "profile_update_notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Profile Updates", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Profile Updated")
            .setContentText("Your name has been changed to $newName.")
            .setSmallIcon(R.drawable.ic_article_placeholder)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1003, notification)
    }
}
