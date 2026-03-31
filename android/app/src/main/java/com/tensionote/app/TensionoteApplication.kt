package com.tensionote.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.tensionote.core.repository.AppGraph

class TensionoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val preferences = getSharedPreferences("tensionote_settings", MODE_PRIVATE)
        val code = preferences.getString("selected_language_code", "system") ?: "system"
        val localeTags = when (code) {
            "zh" -> "zh-Hans"
            "en" -> "en"
            "hi" -> "hi"
            else -> ""
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeTags))
        AppGraph.initialize(this)
    }
}
