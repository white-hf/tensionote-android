package com.tensionote.feature.reminder

import android.Manifest
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.text.format.DateFormat
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.tensionote.R

@Composable
fun ReminderScreen(viewModel: ReminderViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var permissionDenied by rememberSaveable {
        mutableStateOf(
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            permissionDenied = !granted
        }
    )

    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.reminder_title), style = MaterialTheme.typography.headlineMedium)
        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                viewModel.addReminder()
            }
        ) {
            Text(stringResource(R.string.reminder_add))
        }

        if (permissionDenied) {
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(R.string.reminder_notification_permission_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        stringResource(R.string.reminder_notification_permission_body),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
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

        if (state.reminders.isEmpty()) {
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.reminder_empty_title), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.reminder_empty_body), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        state.reminders.forEach { reminder ->
            Card {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        viewModel.updateReminderTime(reminder.id, hour, minute)
                                    },
                                    reminder.hour,
                                    reminder.minute,
                                    DateFormat.is24HourFormat(context)
                                ).show()
                            }
                        ) {
                            Text(reminder.timeLabel, style = MaterialTheme.typography.titleLarge)
                        }
                        Text(
                            stringResource(R.string.reminder_every_day),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = { viewModel.deleteReminder(reminder.id) }) {
                            Text(stringResource(R.string.common_delete))
                        }
                    }
                    Switch(
                        checked = reminder.enabled,
                        onCheckedChange = { viewModel.toggleReminder(reminder.id) }
                    )
                }
            }
        }
    }
}
