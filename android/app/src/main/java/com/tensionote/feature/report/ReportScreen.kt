package com.tensionote.feature.report

import android.app.DatePickerDialog
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import android.widget.Toast
import com.tensionote.R
import com.tensionote.core.export.ReportShareHelper
import com.tensionote.core.model.RecordFormatters
import com.tensionote.core.model.backgroundColor
import com.tensionote.core.model.labelResId
import com.tensionote.core.model.regionalCategory
import com.tensionote.core.model.tintColor
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.FormatStyle

@Composable
fun ReportScreen(viewModel: ReportViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dateFormatter = rememberDateFormatter()
    var pendingExportPath by remember { mutableStateOf<String?>(null) }
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        val sourcePath = pendingExportPath
        pendingExportPath = null

        if (uri == null || sourcePath == null) {
            return@rememberLauncherForActivityResult
        }

        val copied = runCatching {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                File(sourcePath).inputStream().use { input ->
                    input.copyTo(output)
                }
            } != null
        }.getOrDefault(false)

        Toast.makeText(
            context,
            context.getString(
                if (copied) R.string.report_export_saved_success else R.string.report_export_failed
            ),
            Toast.LENGTH_SHORT
        ).show()
    }

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
                                showDatePicker(
                                    context = context,
                                    initialDate = state.startDate,
                                    minDate = null,
                                    maxDate = state.endDate
                                ) { viewModel.updateStartDate(it) }
                            }
                        )
                        dateRow(
                            title = stringResource(R.string.report_custom_to),
                            value = dateFormatter.format(state.endDate),
                            onClick = {
                                showDatePicker(
                                    context = context,
                                    initialDate = state.endDate,
                                    minDate = state.startDate,
                                    maxDate = LocalDate.now()
                                ) { viewModel.updateEndDate(it) }
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
            val category = record.regionalCategory
            Card(colors = CardDefaults.cardColors(containerColor = category.backgroundColor())) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(6.dp)
                            .padding(vertical = 10.dp)
                            .background(category.tintColor(), RoundedCornerShape(3.dp))
                    )
                    Column(
                        modifier = Modifier.padding(start = 14.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            RecordFormatters.formatMeasuredAt(record),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text("${record.systolic}/${record.diastolic}", maxLines = 1)
                        summaryRow(stringResource(R.string.metric_heart_rate), record.heartRate.toString())
                        Text(
                            stringResource(category.labelResId()),
                            style = MaterialTheme.typography.bodySmall,
                            color = category.tintColor(),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    val exported = viewModel.exportPdf()
                    if (exported == null) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.report_export_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        pendingExportPath = exported
                        createDocumentLauncher.launch(File(exported).name)
                    }
                },
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
                        val shared = ReportShareHelper.sharePdf(
                            context = context,
                            file = File(path),
                            subject = viewModel.emailSubject(),
                            body = viewModel.emailBody()
                        )
                        if (!shared) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.report_share_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.report_export_failed),
                            Toast.LENGTH_SHORT
                        ).show()
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(R.string.report_export_saved_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun summaryRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            title,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(value, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            title,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        OutlinedButton(onClick = onClick) {
            Text(value, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit
) {
    val dialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        },
        initialDate.year,
        initialDate.monthValue - 1,
        initialDate.dayOfMonth
    )
    minDate?.let { dialog.datePicker.minDate = it.toStartOfDayMillis() }
    maxDate?.let { dialog.datePicker.maxDate = it.toStartOfDayMillis() }
    dialog.show()
}

private fun LocalDate.toStartOfDayMillis(): Long {
    return atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
