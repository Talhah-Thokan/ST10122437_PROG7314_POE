package com.medassist.app.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.Locale

/**
 * Locale Helper Utility
 * 
 * Manages app language/locale changes for multi-language support.
 * Supports: English (en), Afrikaans (af), isiZulu (zu)
 */
object LocaleHelper {
    
    /**
     * Sets the app locale based on language code
     * 
     * @param context The application context
     * @param languageCode Language code (en, af, zu)
     * @return Context with updated locale
     */
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = when (languageCode.lowercase()) {
            "af" -> Locale("af", "ZA") // Afrikaans - South Africa
            "zu" -> Locale("zu", "ZA") // isiZulu - South Africa
            else -> Locale("en", "ZA") // English - South Africa (default)
        }
        
        Locale.setDefault(locale)
        
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            return context.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION")
            configuration.locale = locale
            @Suppress("DEPRECATION")
            resources.updateConfiguration(configuration, resources.displayMetrics)
            return context
        }
    }
    
    /**
     * Gets the current language code from preferences
     */
    fun getLanguageCode(context: Context): String {
        val prefs = context.getSharedPreferences("MedAssistPrefs", Context.MODE_PRIVATE)
        return prefs.getString("language", "en") ?: "en"
    }
    
    /**
     * Maps language display name to code
     */
    fun getLanguageCodeFromName(languageName: String): String {
        return when (languageName.lowercase()) {
            "english", "engels", "isingisi" -> "en"
            "afrikaans", "isiBhunu" -> "af"
            "isizulu", "zulu" -> "zu"
            else -> "en"
        }
    }
}

