package com.tensionote.feature.record

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tensionote.R
import com.tensionote.core.model.BloodPressureRecord
import com.tensionote.core.model.RecordFormatters
import com.tensionote.core.model.TagLocalization
import com.tensionote.core.model.labelResId
import com.tensionote.core.repository.AppGraph

@Composable
fun RecordDetailScreen(
    record: BloodPressureRecord,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.record_detail_title), style = MaterialTheme.typography.headlineMedium)

        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                detailRow(stringResource(R.string.metric_systolic), record.systolic.toString())
                detailRow(stringResource(R.string.metric_diastolic), record.diastolic.toString())
                detailRow(stringResource(R.string.metric_heart_rate), record.heartRate.toString())
                detailRow(stringResource(R.string.record_status_preview), stringResource(record.status.labelResId()))
                detailRow(stringResource(R.string.record_measured_at), RecordFormatters.formatMeasuredAt(record))
                if (record.tags.isNotEmpty()) {
                    detailRow(stringResource(R.string.record_tags_section), TagLocalization.localize(context, record.tags))
                }
                record.note?.takeIf { it.isNotBlank() }?.let {
                    detailRow(stringResource(R.string.record_note), it)
                }
            }
        }

        Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.record_edit_title))
        }

        Button(
            onClick = {
                AppGraph.bloodPressureRepository.deleteRecord(record.id)
                onDeleted()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.record_delete_button))
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.common_back))
        }
    }
}

@Composable
private fun detailRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
