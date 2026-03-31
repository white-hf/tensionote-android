package com.tensionote.feature.settings

import androidx.annotation.StringRes
import com.tensionote.R

data class SettingsDocument(
    @StringRes val titleResId: Int,
    @StringRes val bodyResId: Int
)

object SettingsDocuments {
    val privacyPolicy = SettingsDocument(
        titleResId = R.string.settings_privacy_policy,
        bodyResId = R.string.document_privacy_content
    )
    val terms = SettingsDocument(
        titleResId = R.string.settings_terms,
        bodyResId = R.string.document_terms_content
    )
    val disclaimer = SettingsDocument(
        titleResId = R.string.settings_disclaimer,
        bodyResId = R.string.document_disclaimer_content
    )
}

