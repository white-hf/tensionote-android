package com.tensionote.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
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
    val haptic = LocalHapticFeedback.current
    val evaluator = remember { RegionalBloodPressureEvaluator() }
    val displayCategory = state.draftCategory ?: state.selectedCategory
    val systolicHighCount = state.trendRecords.count {
        evaluator.standard.isSystolicAboveHypertensionThreshold(it.systolic)
    }
    val diastolicHighCount = state.trendRecords.count {
        evaluator.standard.isDiastolicAboveHypertensionThreshold(it.diastolic)
    }

    LaunchedEffect(state.showSaveSuccess) {
        if (state.showSaveSuccess) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (state.showSaveSuccess) {
                            ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        } else {
                            ButtonDefaults.buttonColors()
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (state.showSaveSuccess) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Text(stringResource(R.string.common_save_success))
                            } else {
                                Text(stringResource(R.string.common_save_now))
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = state.feedbackMessageKey != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        state.feedbackMessageKey?.let { key ->
                            val resId = remember(key) {
                                when (key) {
                                    "feedback_first_record" -> R.string.feedback_first_record
                                    "feedback_normal" -> R.string.feedback_normal
                                    "feedback_stable_high" -> R.string.feedback_stable_high
                                    "feedback_improving" -> R.string.feedback_improving
                                    "feedback_sudden_high" -> R.string.feedback_sudden_high
                                    else -> 0
                                }
                            }
                            if (resId != 0) {
                                Text(
                                    stringResource(resId),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
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
                                R.string.trend_chart_legend_compact_rule,
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
