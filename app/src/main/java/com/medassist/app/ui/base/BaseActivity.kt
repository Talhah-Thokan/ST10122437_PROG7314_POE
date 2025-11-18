package com.medassist.app.ui.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.medassist.app.utils.LocaleHelper
import com.medassist.app.utils.PreferenceManager

/**
 * A base activity that handles applying the saved locale on creation.
 * All other activities should extend this class to ensure language consistency.
 */
open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Get the saved language code from preferences
        val languageCode = PreferenceManager(newBase).getLanguage()
        // Create a new context with the specified locale
        val context = LocaleHelper.setLocale(newBase, languageCode)
        // Attach the new context to the activity
        super.attachBaseContext(context)
    }
}
