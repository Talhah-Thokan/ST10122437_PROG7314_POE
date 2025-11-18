package com.medassist.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.medassist.app.databinding.ActivityMainBinding
import com.medassist.app.ui.base.BaseActivity
import com.medassist.app.ui.fragments.ArticlesFragment
import com.medassist.app.ui.fragments.BookingsFragment
import com.medassist.app.ui.fragments.HomeFragment
import com.medassist.app.ui.screens.auth.LoginActivity
import com.medassist.app.utils.PreferenceManager
import com.medassist.app.utils.SyncManager
import com.medassist.app.utils.NetworkUtils

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager

    companion object {
        private const val TAG = "MedAssistMainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceManager = PreferenceManager(this)

        applyTheme()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "MainActivity created")

        setupBottomNavigation()

        if (!preferenceManager.isUserLoggedIn()) {
            navigateToLogin()
            return
        }

        val userName = preferenceManager.getUserName()
        Toast.makeText(this, "Welcome back, $userName!", Toast.LENGTH_LONG).show()

        autoSyncIfNeeded()
    }

    private fun autoSyncIfNeeded() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            Log.d(TAG, "🌐 Network available - initiating auto-sync...")
            SyncManager(this).autoSyncIfNeeded()
        } else {
            Log.d(TAG, "📴 No network - skipping auto-sync")
        }
    }

    override fun onResume() {
        super.onResume()
        autoSyncIfNeeded()
    }

    private fun applyTheme() {
        val isDarkTheme = preferenceManager.isDarkThemeEnabled()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_articles -> {
                    loadFragment(ArticlesFragment())
                    true
                }
                R.id.nav_bookings -> {
                    loadFragment(BookingsFragment())
                    true
                }
                else -> false
            }
        }

        loadFragment(HomeFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
