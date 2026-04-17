package com.tensionote.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tensionote.R
import com.tensionote.core.model.RegionalBloodPressureEvaluator
import com.tensionote.core.model.labelResId
import com.tensionote.core.model.tintColor
import com.tensionote.feature.trend.TrendChart

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onDetailedEntryClick: () -> Unit,
    onTrendClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val evaluator = remember { RegionalBloodPressureEvaluator() }
    val displayCategory = state.draftCategory ?: state.selectedCategory
    val systolicHighCount = state.trendRecords.count {
        evaluator.standard.isSystolicAboveHypertensionThreshold(it.systolic)
    }
    val diastolicHighCount = state.trendRecords.count {
        evaluator.standard.isDiastolicAboveHypertensionThreshold(it.diastolic)
    }

    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
            .imePadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.home_quick_entry_title), style = MaterialTheme.typography.headlineSmall)
                        OutlinedButton(onClick = onDetailedEntryClick) {
                            Text(stringResource(R.string.home_detailed_entry_button))
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.systolicInput,
                            onValueChange = viewModel::updateSystolic,
                            label = { Text(stringResource(R.string.metric_systolic)) },
                            placeholder = { Text(stringResource(R.string.common_number_placeholder)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) }
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = state.diastolicInput,
                            onValueChange = viewModel::updateDiastolic,
                            label = { Text(stringResource(R.string.metric_diastolic)) },
                            placeholder = { Text(stringResource(R.string.common_number_placeholder)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) }
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = state.heartRateInput,
                        onValueChange = viewModel::updateHeartRate,
                        label = { Text(stringResource(R.string.metric_heart_rate)) },
                        placeholder = { Text(stringResource(R.string.common_number_placeholder)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            viewModel.saveQuickRecord()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.common_save_now))
                    }

                    state.validationMessageKey?.let {
                        Text(
                            when (it) {
                                "validation_enter_valid_numbers" -> stringResource(R.string.validation_enter_valid_numbers)
                                "validation_out_of_range" -> stringResource(R.string.validation_out_of_range)
                                else -> it
                            },
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        item {
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.home_recent_two_weeks_trend), style = MaterialTheme.typography.headlineSmall)
                        OutlinedButton(onClick = onTrendClick) {
                            Text(stringResource(R.string.home_view_all_trend))
                        }
                    }

                    if (state.trendRecords.isEmpty()) {
                        Text(stringResource(R.string.trend_empty_title), style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(R.string.trend_empty_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        TrendChart(records = state.trendRecords)
                        Text(
                            stringResource(displayCategory.labelResId()),
                            style = MaterialTheme.typography.titleMedium,
                            color = displayCategory.tintColor()
                        )
                        Text(
                            stringResource(
                                R.string.trend_chart_legend_thresholds,
                                evaluator.standard.hypertensionSystolicThreshold,
                                evaluator.standard.hypertensionDiastolicThreshold
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            if (systolicHighCount > 0 || diastolicHighCount > 0) {
                                stringResource(R.string.trend_summary_counts, systolicHighCount, diastolicHighCount)
                            } else {
                                stringResource(R.string.trend_summary_normal)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

    }
}
