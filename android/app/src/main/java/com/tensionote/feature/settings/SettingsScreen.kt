package com.tensionote.feature.settings

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.tensionote.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onOpenDocument: (SettingsDocument) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val notificationDenied = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != android.content.pm.PackageManager.PERMISSION_GRANTED
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
                    .clickable { viewModel.selectLanguage(code) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = state.selectedLanguageCode == code, onClick = { viewModel.selectLanguage(code) })
                Text(
                    label,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp)
                )
            }
        }

        if (notificationDenied) {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.settings_notifications_title), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.settings_notifications_body), style = MaterialTheme.typography.bodyMedium)
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                putExtra("app_package", context.packageName)
                                putExtra("app_uid", context.applicationInfo.uid)
                            }
                            try {
                                context.startActivity(intent)
                            } catch (_: ActivityNotFoundException) {
                                context.startActivity(
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = android.net.Uri.fromParts("package", context.packageName, null)
                                    }
                                )
                            }
                        }
                    ) {
                        Text(stringResource(R.string.reminder_notification_permission_action))
                    }
                }
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
