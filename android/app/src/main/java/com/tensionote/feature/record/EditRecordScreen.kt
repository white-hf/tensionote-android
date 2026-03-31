package com.tensionote.feature.record

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tensionote.R
import com.tensionote.core.model.BloodPressureRecord
import com.tensionote.core.model.labelResId
import com.tensionote.core.repository.AppGraph
import com.tensionote.core.rules.BloodPressureStatusEvaluator
import com.tensionote.core.rules.RecordInputValidator

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditRecordScreen(
    record: BloodPressureRecord,
    onBack: () -> Unit
) {
    var systolic by remember { mutableStateOf(record.systolic.toString()) }
    var diastolic by remember { mutableStateOf(record.diastolic.toString()) }
    var heartRate by remember { mutableStateOf(record.heartRate.toString()) }
    var note by remember { mutableStateOf(record.note.orEmpty()) }
    var selectedTags by remember { mutableStateOf(record.tags.toSet()) }
    var measuredAt by remember { mutableStateOf(record.measuredAt) }
    var validationMessage by remember { mutableStateOf<String?>(null) }
    val validator = remember { RecordInputValidator() }
    val evaluator = remember { BloodPressureStatusEvaluator() }
    val validationOutOfRange = stringResource(R.string.validation_out_of_range)
    val validationInvalidNumbers = stringResource(R.string.validation_enter_valid_numbers)
    val tagItems = listOf(
        "tag_morning" to stringResource(R.string.tag_morning),
        "tag_afternoon" to stringResource(R.string.tag_afternoon),
        "tag_evening" to stringResource(R.string.tag_evening),
        "tag_after_meal" to stringResource(R.string.tag_after_meal),
        "tag_after_exercise" to stringResource(R.string.tag_after_exercise),
        "tag_after_medication" to stringResource(R.string.tag_after_medication),
        "tag_discomfort" to stringResource(R.string.tag_discomfort)
    )

    val previewStatus = remember(systolic, diastolic, record.status) {
        val systolicValue = systolic.toIntOrNull()
        val diastolicValue = diastolic.toIntOrNull()
        if (systolicValue == null || diastolicValue == null) record.status else evaluator.evaluate(systolicValue, diastolicValue)
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.record_edit_title), style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = systolic,
            onValueChange = {
                systolic = it
                validationMessage = null
            },
            label = { Text(stringResource(R.string.metric_systolic)) },
            placeholder = { Text(stringResource(R.string.common_number_placeholder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = diastolic,
            onValueChange = {
                diastolic = it
                validationMessage = null
            },
            label = { Text(stringResource(R.string.metric_diastolic)) },
            placeholder = { Text(stringResource(R.string.common_number_placeholder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = heartRate,
            onValueChange = {
                heartRate = it
                validationMessage = null
            },
            label = { Text(stringResource(R.string.metric_heart_rate)) },
            placeholder = { Text(stringResource(R.string.common_number_placeholder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        DateTimePickerField(
            measuredAt = measuredAt,
            onChanged = { measuredAt = it },
            label = stringResource(R.string.record_measured_at)
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tagItems.forEach { (key, label) ->
                FilterChip(
                    selected = selectedTags.contains(key),
                    onClick = {
                        selectedTags = if (selectedTags.contains(key)) {
                            selectedTags - key
                        } else {
                            selectedTags + key
                        }
                    },
                    label = { Text(label) }
                )
            }
        }
        OutlinedTextField(
            value = note,
            onValueChange = {
                note = it
                validationMessage = null
            },
            label = { Text(stringResource(R.string.record_note)) },
            modifier = Modifier.fillMaxWidth()
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.record_status_preview),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(previewStatus.labelResId()),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Button(
            onClick = {
                val systolicValue = systolic.toIntOrNull()
                val diastolicValue = diastolic.toIntOrNull()
                val heartRateValue = heartRate.toIntOrNull()
                if (systolicValue != null && diastolicValue != null && heartRateValue != null) {
                    if (!validator.isValid(systolicValue, diastolicValue, heartRateValue)) {
                        validationMessage = validationOutOfRange
                        return@Button
                    }
                    AppGraph.bloodPressureRepository.updateRecord(
                        id = record.id,
                        systolic = systolicValue,
                        diastolic = diastolicValue,
                        heartRate = heartRateValue,
                        measuredAt = measuredAt,
                        tags = selectedTags.toList(),
                        note = note.ifEmpty { null }
                    )
                    validationMessage = null
                    onBack()
                } else {
                    validationMessage = validationInvalidNumbers
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.record_save_button))
        }

        validationMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.common_back))
        }
    }
}
