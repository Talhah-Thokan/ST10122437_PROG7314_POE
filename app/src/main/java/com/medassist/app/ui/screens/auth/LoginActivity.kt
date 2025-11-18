package com.medassist.app.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.button.MaterialButton
import com.medassist.app.MainActivity
import com.medassist.app.R
import com.medassist.app.data.firebase.FirebaseAuthManager
import com.medassist.app.ui.base.BaseActivity
import com.medassist.app.utils.BiometricHelper
import com.medassist.app.utils.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {
    
    private lateinit var googleSignInButton: MaterialButton
    private lateinit var guestButton: MaterialButton
    private lateinit var biometricButton: MaterialButton
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var firebaseAuthManager: FirebaseAuthManager
    private lateinit var biometricHelper: BiometricHelper
    
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleGoogleSignInResult(result.resultCode, result.data)
    }
    
    companion object {
        private const val TAG = "MedAssistLoginActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        preferenceManager = PreferenceManager(this)
        firebaseAuthManager = FirebaseAuthManager(this)
        biometricHelper = BiometricHelper(this)
        
        Log.d(TAG, "LoginActivity created")
        
        setupViews()
        setupClickListeners()
        
        checkBiometricLogin()
        
        Toast.makeText(this, "Welcome to MedAssist!", Toast.LENGTH_LONG).show()
    }
    
    private fun setupViews() {
        googleSignInButton = findViewById(R.id.googleSignInButton)
        guestButton = findViewById(R.id.guestButton)
        biometricButton = findViewById(R.id.biometricButton)
        
        if (biometricHelper.isBiometricAvailable()) {
            biometricButton.visibility = android.view.View.VISIBLE
            Log.d(TAG, "✅ Biometric authentication available")
        } else {
            biometricButton.visibility = android.view.View.GONE
            Log.d(TAG, "⚠️ Biometric authentication not available: ${biometricHelper.getBiometricStatusMessage()}")
        }
        
        Log.d(TAG, "Views setup complete")
    }
    
    private fun setupClickListeners() {
        googleSignInButton.setOnClickListener {
            Log.d(TAG, "Google Sign-In button clicked")
            startGoogleSignIn()
        }
        
        guestButton.setOnClickListener {
            Log.d(TAG, "Guest button clicked")
            handleGuestLogin()
        }
        
        biometricButton.setOnClickListener {
            Log.d(TAG, "Biometric button clicked")
            handleBiometricLogin()
        }
        
        Log.d(TAG, "Click listeners setup complete")
    }
    
    private fun checkBiometricLogin() {
        if (preferenceManager.isUserLoggedIn() && 
            preferenceManager.isBiometricEnabled() && 
            biometricHelper.isBiometricAvailable()) {
            
            Log.d(TAG, "🔄 User was previously logged in with biometric enabled - showing prompt")
            
            findViewById<android.view.View>(R.id.biometricButton).postDelayed({
                showBiometricPrompt(
                    title = getString(R.string.biometric_login_title),
                    subtitle = getString(R.string.biometric_login_subtitle)
                )
            }, 500)
        }
    }
    
    private fun handleBiometricLogin() {
        if (!biometricHelper.isBiometricAvailable()) {
            Toast.makeText(
                this,
                biometricHelper.getBiometricStatusMessage(),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        
        if (!preferenceManager.isUserLoggedIn()) {
            Toast.makeText(
                this,
                getString(R.string.biometric_login_first_time),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        
        showBiometricPrompt(
            title = getString(R.string.biometric_login_title),
            subtitle = getString(R.string.biometric_login_subtitle)
        )
    }
    
    private fun showBiometricPrompt(title: String, subtitle: String) {
        biometricHelper.showBiometricPrompt(
            title = title,
            subtitle = subtitle,
            onSuccess = {
                Log.d(TAG, "✅ Biometric authentication successful")
                Toast.makeText(this, getString(R.string.biometric_login_success), Toast.LENGTH_SHORT).show()
                navigateToMain()
            },
            onError = { errorMessage ->
                Log.e(TAG, "❌ Biometric authentication failed: $errorMessage")
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        )
    }
    
    private fun startGoogleSignIn() {
        try {
            val signInIntent = firebaseAuthManager.getSignInIntent()
            googleSignInLauncher.launch(signInIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting Google Sign-In", e)
            Toast.makeText(this, "Google Sign-In not available", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun handleGoogleSignInResult(resultCode: Int, data: Intent?) {
        Log.d(TAG, "🔄 handleGoogleSignInResult: resultCode=$resultCode, data=${if (data != null) "present" else "null"}")
        
        if (data != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    Log.d(TAG, "🔄 Processing Google Sign-In result (resultCode=$resultCode)...")
                    
                    if (resultCode == RESULT_OK) {
                        Toast.makeText(this@LoginActivity, "Signing in with Google...", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w(TAG, "⚠️ Result code is not OK ($resultCode), but attempting to process result anyway...")
                    }
                    
                    val result = firebaseAuthManager.handleSignInResult(data)
                    result.onSuccess { user ->
                        Log.d(TAG, "✅ Google Sign-In successful: ${user.displayName}")
                        
                        preferenceManager.setUserLoggedIn(true)
                        preferenceManager.setUserName(user.displayName ?: "Google User")
                        preferenceManager.setUserEmail(user.email ?: "")
                        
                        if (biometricHelper.isBiometricAvailable()) {
                            preferenceManager.setBiometricEnabled(true)
                            Log.d(TAG, "✅ Biometric enabled for future logins")
                        }
                        
                        Toast.makeText(this@LoginActivity, "Welcome, ${user.displayName}!", Toast.LENGTH_LONG).show()
                        navigateToMain()
                    }.onFailure { exception ->
                        Log.e(TAG, "❌ Google Sign-In failed: ${exception.message}", exception)
                        
                        val errorMsg = when {
                            exception.message?.contains("10") == true || 
                            exception.message?.contains("Developer error") == true -> 
                                "❌ Firebase configuration error!\n\nPlease add SHA-1 fingerprint to Firebase Console.\n\nSee GOOGLE_SIGNIN_TROUBLESHOOTING.md for instructions."
                            exception.message?.contains("12501") == true ->
                                "Sign-in cancelled by user."
                            exception.message?.contains("7") == true || 
                            exception.message?.contains("Network") == true -> 
                                "Network error. Please check your internet connection."
                            exception.message?.contains("ID token is null") == true ->
                                "❌ Configuration error!\n\nSHA-1 fingerprint missing in Firebase.\n\nSee GOOGLE_SIGNIN_TROUBLESHOOTING.md"
                            exception.message?.contains("Sign-in failed") == true ->
                                "Sign-in failed: ${exception.message}"
                            else -> 
                                "❌ Sign-in failed!\n\nLikely cause: Missing SHA-1 fingerprint in Firebase Console.\n\nError: ${exception.message ?: "Unknown error"}\n\nSee GOOGLE_SIGNIN_TROUBLESHOOTING.md"
                        }
                        
                        Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Unexpected error during sign-in: ${e.message}", e)
                    
                    val errorMsg = if (resultCode == RESULT_CANCELED) {
                        "❌ Google Sign-In failed!\n\nThis usually means:\n1. SHA-1 fingerprint not added to Firebase\n2. Wrong web client ID\n\nSee GOOGLE_SIGNIN_TROUBLESHOOTING.md\n\nError: ${e.message ?: "Unknown"}"
                    } else {
                        "Sign-in failed: ${e.message ?: "Unknown error"}"
                    }
                    
                    Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.w(TAG, "⚠️ User cancelled Google Sign-In (no data returned)")
            Toast.makeText(this, "Google Sign-In cancelled", Toast.LENGTH_SHORT).show()
        } else {
            Log.e(TAG, "❌ Unknown result code: $resultCode, no data")
            Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun handleGuestLogin() {
        Toast.makeText(this, "Continuing as guest...", Toast.LENGTH_SHORT).show()
        
        preferenceManager.setUserLoggedIn(true)
        preferenceManager.setUserName("Guest User")
        preferenceManager.setUserEmail("guest@medassist.com")
        
        navigateToMain()
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
