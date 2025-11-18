package com.medassist.app.utils

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Helper class for Biometric Authentication
 * Handles fingerprint, face unlock, and other biometric authentication methods
 */
class BiometricHelper(private val activity: FragmentActivity) {

    companion object {
        private const val TAG = "BiometricHelper"
    }

    /**
     * Checks if biometric authentication is available on the device
     * @return true if biometric hardware is available and enrolled
     */
    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(activity)
        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )

        return when (canAuthenticate) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d(TAG, "✅ Biometric authentication is available")
                true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.w(TAG, "⚠️ No biometric hardware available")
                false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.w(TAG, "⚠️ Biometric hardware is unavailable")
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.w(TAG, "⚠️ No biometrics enrolled")
                false
            }
            else -> {
                Log.w(TAG, "⚠️ Biometric authentication not available")
                false
            }
        }
    }

    /**
     * Gets a user-friendly message about biometric availability
     */
    fun getBiometricStatusMessage(): String {
        val biometricManager = BiometricManager.from(activity)
        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )

        return when (canAuthenticate) {
            BiometricManager.BIOMETRIC_SUCCESS -> "Biometric authentication available"
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "No biometric hardware found"
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "Biometric hardware unavailable"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "No biometrics enrolled. Please set up fingerprint or face unlock in device settings."
            else -> "Biometric authentication not available"
        }
    }

    /**
     * Shows biometric prompt for authentication
     * @param title Title for the biometric prompt
     * @param subtitle Subtitle for the biometric prompt
     * @param onSuccess Callback when authentication succeeds
     * @param onError Callback when authentication fails
     */
    fun showBiometricPrompt(
        title: String = "Biometric Authentication",
        subtitle: String = "Use your fingerprint or face to continue",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isBiometricAvailable()) {
            onError(getBiometricStatusMessage())
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d(TAG, "✅ Biometric authentication succeeded")
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.e(TAG, "❌ Biometric authentication error: $errorCode - $errString")
                    
                    val errorMessage = when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> "Authentication cancelled"
                        BiometricPrompt.ERROR_USER_CANCELED -> "Authentication cancelled by user"
                        BiometricPrompt.ERROR_LOCKOUT -> "Too many failed attempts. Please try again later."
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> "Biometric authentication is permanently locked. Please use password."
                        else -> "Authentication failed: $errString"
                    }
                    
                    onError(errorMessage)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.w(TAG, "⚠️ Biometric authentication failed")
                    onError("Authentication failed. Please try again.")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            .build()

        Log.d(TAG, "🔄 Showing biometric prompt...")
        biometricPrompt.authenticate(promptInfo)
    }
}

