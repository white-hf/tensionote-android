package com.tensionote.feature.trend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tensionote.R
import com.tensionote.core.model.RecordFormatters
import com.tensionote.core.model.RegionalBloodPressureEvaluator
import com.tensionote.core.model.labelResId
import com.tensionote.core.model.regionalCategory
import com.tensionote.core.model.tintColor
import com.tensionote.feature.home.HomeViewModel

@Composable
fun TrendScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val evaluator = androidx.compose.runtime.remember { RegionalBloodPressureEvaluator() }
    val systolicHighCount = state.trendRecords.count {
        evaluator.standard.isSystolicAboveHypertensionThreshold(it.systolic)
    }
    val diastolicHighCount = state.trendRecords.count {
        evaluator.standard.isDiastolicAboveHypertensionThreshold(it.diastolic)
    }

    LazyColumn(
        modifier = Modifier
            .padding(20.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.trend_title), style = MaterialTheme.typography.headlineMedium)
                OutlinedButton(onClick = onBack) {
                    Text(stringResource(R.string.common_back))
                }
            }
        }

        item {
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.trendRecords.isEmpty()) {
                        Text(stringResource(R.string.trend_empty_title), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.trend_empty_body), style = MaterialTheme.typography.bodyMedium)
                    } else {
                        TrendChart(records = state.trendRecords)
                        Text(
                            stringResource(state.selectedCategory.labelResId()),
                            style = MaterialTheme.typography.titleMedium,
                            color = state.selectedCategory.tintColor()
                        )
                        Text(
                            stringResource(
                                R.string.trend_chart_legend_compact_rule,
                                evaluator.standard.hypertensionSystolicThreshold,
                                evaluator.standard.hypertensionDiastolicThreshold
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (systolicHighCount > 0 || diastolicHighCount > 0) {
                            Text(stringResource(R.string.trend_summary_counts, systolicHighCount, diastolicHighCount))
                        } else {
                            Text(stringResource(R.string.trend_summary_normal))
                        }
                    }
                }
            }
        }

        items(state.trendRecords) { record ->
            Card {
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.selectRecord(record) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(RecordFormatters.formatMeasuredAt(record), style = MaterialTheme.typography.bodySmall)
                        Text("${record.systolic}/${record.diastolic}", style = MaterialTheme.typography.titleLarge)
                        Text(
                            stringResource(record.regionalCategory.labelResId()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = record.regionalCategory.tintColor()
                        )
                    }
                }
            }
        }
    }
}
