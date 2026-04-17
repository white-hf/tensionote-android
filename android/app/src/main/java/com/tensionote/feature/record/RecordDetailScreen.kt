package com.tensionote.feature.record

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tensionote.R
import com.tensionote.core.model.BloodPressureRecord
import com.tensionote.core.model.RecordFormatters
import com.tensionote.core.model.TagLocalization
import com.tensionote.core.model.backgroundColor
import com.tensionote.core.model.labelResId
import com.tensionote.core.model.regionalCategory
import com.tensionote.core.model.tintColor
import com.tensionote.core.repository.AppGraph

@Composable
fun RecordDetailScreen(
    record: BloodPressureRecord,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit
) {
    val context = LocalContext.current
    val category = record.regionalCategory
    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.record_detail_title), style = MaterialTheme.typography.headlineMedium)

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
                        .padding(vertical = 10.dp, horizontal = 0.dp)
                        .background(category.tintColor(), RoundedCornerShape(3.dp))
                )
                Column(
                    modifier = Modifier.padding(start = 14.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    detailRow(stringResource(R.string.metric_systolic), record.systolic.toString())
                    detailRow(stringResource(R.string.metric_diastolic), record.diastolic.toString())
                    detailRow(stringResource(R.string.metric_heart_rate), record.heartRate.toString())
                    detailRow(
                        stringResource(R.string.record_status_preview),
                        stringResource(category.labelResId()),
                        valueColor = category.tintColor()
                    )
                    detailRow(stringResource(R.string.record_measured_at), RecordFormatters.formatMeasuredAt(record))
                    if (record.tags.isNotEmpty()) {
                        detailRow(stringResource(R.string.record_tags_section), TagLocalization.localize(context, record.tags))
                    }
                    record.note?.takeIf { it.isNotBlank() }?.let {
                        detailRow(stringResource(R.string.record_note), it)
                    }
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
private fun detailRow(title: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
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
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            color = valueColor,
            textAlign = TextAlign.End,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}
