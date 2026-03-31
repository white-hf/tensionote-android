package com.tensionote.feature.report

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tensionote.R
import com.tensionote.core.export.ReportShareHelper
import com.tensionote.core.model.RecordFormatters
import com.tensionote.core.model.labelResId
import java.io.File
import java.time.LocalDate
import java.time.format.FormatStyle

@Composable
fun ReportScreen(viewModel: ReportViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dateFormatter = rememberDateFormatter()

    LazyColumn(
        modifier = Modifier
            .padding(20.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(stringResource(R.string.tab_report), style = MaterialTheme.typography.headlineMedium)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(7, 14, 30).forEach { days ->
                    val label = when (days) {
                        7 -> stringResource(R.string.report_range_7_days)
                        14 -> stringResource(R.string.report_range_14_days)
                        else -> stringResource(R.string.report_range_30_days)
                    }
                    if (state.selectedDays == days) {
                        Button(
                            onClick = { viewModel.selectDays(days) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(label)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.selectDays(days) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }

        item {
            if (state.isCustomRange) {
                Button(
                    onClick = viewModel::selectCustomRange,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.report_range_custom))
                }
            } else {
                OutlinedButton(
                    onClick = viewModel::selectCustomRange,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.report_range_custom))
                }
            }
        }

        if (state.isCustomRange) {
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        dateRow(
                            title = stringResource(R.string.report_custom_from),
                            value = dateFormatter.format(state.startDate),
                            onClick = {
                                showDatePicker(context, state.startDate) { viewModel.updateStartDate(it) }
                            }
                        )
                        dateRow(
                            title = stringResource(R.string.report_custom_to),
                            value = dateFormatter.format(state.endDate),
                            onClick = {
                                showDatePicker(context, state.endDate) { viewModel.updateEndDate(it) }
                            }
                        )
                    }
                }
            }
        }

        item {
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.report_summary_title), style = MaterialTheme.typography.titleMedium)
                    summaryRow(stringResource(R.string.report_metric_count), state.summary.count.toString())
                    summaryRow(stringResource(R.string.report_metric_average_systolic), state.summary.averageSystolic.toString())
                    summaryRow(stringResource(R.string.report_metric_average_diastolic), state.summary.averageDiastolic.toString())
                    summaryRow(stringResource(R.string.report_metric_average_heart_rate), state.summary.averageHeartRate.toString())
                }
            }
        }

        if (state.records.isEmpty()) {
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.report_empty_title), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.report_empty_body), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        if (state.records.isNotEmpty()) {
            item {
                Text(stringResource(R.string.report_recent_records_title), style = MaterialTheme.typography.titleMedium)
            }
        }

        items(state.records.take(5)) { record ->
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(RecordFormatters.formatMeasuredAt(record), style = MaterialTheme.typography.bodySmall)
                    Text("${record.systolic}/${record.diastolic}")
                    summaryRow(stringResource(R.string.metric_heart_rate), record.heartRate.toString())
                    Text(stringResource(record.status.labelResId()), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            Button(
                onClick = viewModel::exportPdf,
                enabled = state.records.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.report_export_pdf))
            }
        }

        item {
            Button(
                onClick = {
                    val path = state.exportedFilePath ?: viewModel.exportPdf()
                    if (path != null) {
                        ReportShareHelper.sharePdf(
                            context = context,
                            file = File(path),
                            subject = viewModel.emailSubject(),
                            body = viewModel.emailBody()
                        )
                    }
                },
                enabled = state.records.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.report_share_email))
            }
        }

        state.exportedFileName?.let { fileName ->
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.report_export_result_prefix),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun summaryRow(title: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(title)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
        Text(value)
    }
}

@Composable
private fun dateRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        OutlinedButton(onClick = onClick) {
            Text(value)
        }
    }
}

@Composable
private fun rememberDateFormatter(): java.time.format.DateTimeFormatter {
    val locale = androidx.compose.ui.text.intl.Locale.current.platformLocale
    return java.time.format.DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
}

private fun showDatePicker(
    context: android.content.Context,
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        },
        initialDate.year,
        initialDate.monthValue - 1,
        initialDate.dayOfMonth
    ).show()
}
