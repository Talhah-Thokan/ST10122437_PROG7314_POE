package com.medassist.app.ui.screens.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.medassist.app.MainActivity
import com.medassist.app.R
import com.medassist.app.data.firebase.FirestoreDataSeederActivity
import com.medassist.app.ui.base.BaseActivity
import com.medassist.app.ui.screens.auth.LoginActivity
import com.medassist.app.utils.PreferenceManager
import com.medassist.app.utils.SyncManager

class SettingsActivity : BaseActivity() {

    private lateinit var backButton: MaterialButton
    private lateinit var logoutButton: MaterialButton
    private lateinit var seedDatabaseButton: MaterialButton
    private lateinit var syncDataButton: MaterialButton
    private lateinit var testNotificationButton: MaterialButton
    private lateinit var darkThemeSwitch: SwitchMaterial
    private lateinit var notificationsSwitch: SwitchMaterial
    private lateinit var languageSpinner: MaterialAutoCompleteTextView
    private lateinit var preferenceManager: PreferenceManager

    companion object {
        private const val TAG = "SettingsActivity"
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        preferenceManager = PreferenceManager(this)

        setupViews()
        setupClickListeners()
        loadCurrentSettings()

        Log.d(TAG, "SettingsActivity created")
    }

    private fun setupViews() {
        backButton = findViewById(R.id.backButton)
        logoutButton = findViewById(R.id.logoutButton)
        seedDatabaseButton = findViewById(R.id.seedDatabaseButton)
        syncDataButton = findViewById(R.id.syncDataButton)
        testNotificationButton = findViewById(R.id.testNotificationButton)
        darkThemeSwitch = findViewById(R.id.darkThemeSwitch)
        notificationsSwitch = findViewById(R.id.notificationsSwitch)
        languageSpinner = findViewById(R.id.languageSpinner)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }

        seedDatabaseButton.setOnClickListener {
            startActivity(Intent(this, FirestoreDataSeederActivity::class.java))
        }

        syncDataButton.setOnClickListener {
            Log.d(TAG, "🔄 Manual sync button clicked")
            SyncManager(this).syncArticles { success, message ->
                Log.d(TAG, if (success) "✅ Sync completed: $message" else "❌ Sync failed: $message")
            }
        }

        testNotificationButton.setOnClickListener {
            Log.d(TAG, "🔔 Test notification button clicked")
            handleTestNotification()
        }

        logoutButton.setOnClickListener { handleLogout() }

        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.setDarkThemeEnabled(isChecked)
            applyTheme()
            Toast.makeText(this, "Theme preference saved", Toast.LENGTH_SHORT).show()
        }

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.setNotificationsEnabled(isChecked)
            Toast.makeText(this, "Notification preference saved", Toast.LENGTH_SHORT).show()
        }

        languageSpinner.setOnClickListener { 
            languageSpinner.showDropDown()
        }

        languageSpinner.setOnItemClickListener { _, _, position, _ ->
            val languageCodes = arrayOf("en", "af", "zu")

            if (position < languageCodes.size) {
                val selectedLanguageCode = languageCodes[position]
                preferenceManager.setLanguage(selectedLanguageCode)

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun loadCurrentSettings() {
        darkThemeSwitch.isChecked = preferenceManager.isDarkThemeEnabled()
        notificationsSwitch.isChecked = preferenceManager.areNotificationsEnabled()

        val currentLanguage = preferenceManager.getLanguage()
        val languages = arrayOf(
            getString(R.string.language_english),
            getString(R.string.language_afrikaans),
            getString(R.string.language_zulu)
        )
        val languageCodes = arrayOf("en", "af", "zu")
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, languages)
        languageSpinner.setAdapter(adapter)

        val currentIndex = languageCodes.indexOf(currentLanguage)
        if (currentIndex != -1) {
            languageSpinner.setText(languages[currentIndex], false)
        }
    }

    private fun handleTestNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
        } else {
            showTestNotification()
        }
    }

    private fun showTestNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medassist_notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "MedAssist Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)

            if (notificationManager.getNotificationChannel(channelId).importance == NotificationManager.IMPORTANCE_NONE) {
                Toast.makeText(this, "Notifications disabled. Please enable them in settings.", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intent)
                return
            }
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = android.app.PendingIntent.getActivity(this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("🔔 Test Notification")
            .setContentText("This is a test notification from MedAssist.")
            .setSmallIcon(R.drawable.ic_article_placeholder)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(9999, notification)
        Toast.makeText(this, "Test notification sent!", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            showTestNotification()
        }
    }

    private fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (preferenceManager.isDarkThemeEnabled()) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun handleLogout() {
        preferenceManager.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
