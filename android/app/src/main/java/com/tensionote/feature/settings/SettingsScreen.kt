package com.tensionote.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tensionote.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onOpenDocument: (SettingsDocument) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val options = listOf(
        "system" to stringResource(R.string.settings_language_system),
        "zh" to stringResource(R.string.settings_language_zh),
        "en" to stringResource(R.string.settings_language_en),
        "hi" to stringResource(R.string.settings_language_hi)
    )

    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.tab_settings), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.settings_language_title), style = MaterialTheme.typography.titleMedium)

        options.forEach { (code, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectLanguage(code) }
            ) {
                RadioButton(selected = state.selectedLanguageCode == code, onClick = { viewModel.selectLanguage(code) })
                Text(label, modifier = Modifier.padding(top = 12.dp))
            }
        }

        Text(stringResource(R.string.settings_documents_title), style = MaterialTheme.typography.titleMedium)

        documentCard(
            title = stringResource(R.string.settings_privacy_policy),
            onClick = { onOpenDocument(SettingsDocuments.privacyPolicy) }
        )
        documentCard(
            title = stringResource(R.string.settings_terms),
            onClick = { onOpenDocument(SettingsDocuments.terms) }
        )
        documentCard(
            title = stringResource(R.string.settings_disclaimer),
            onClick = { onOpenDocument(SettingsDocuments.disclaimer) }
        )
    }
}

@Composable
private fun documentCard(
    title: String,
    onClick: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
