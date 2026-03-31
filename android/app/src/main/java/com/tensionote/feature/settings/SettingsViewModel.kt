package com.tensionote.feature.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val selectedLanguageCode: String = "system"
)

class SettingsViewModel(
    private val context: Context
) : ViewModel() {
    private val preferences = context.getSharedPreferences("tensionote_settings", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            selectedLanguageCode = preferences.getString("selected_language_code", "system") ?: "system"
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState

    fun selectLanguage(code: String) {
        preferences.edit().putString("selected_language_code", code).apply()
        _uiState.update { it.copy(selectedLanguageCode = code) }
        val localeTags = when (code) {
            "zh" -> "zh-Hans"
            "en" -> "en"
            "hi" -> "hi"
            else -> ""
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeTags))
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(context.applicationContext) as T
                }
            }
        }
    }
}
