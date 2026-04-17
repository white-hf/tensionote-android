package com.tensionote.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tensionote.R
import com.tensionote.core.model.BloodPressureRecord
import com.tensionote.core.model.RecordFormatters
import com.tensionote.core.model.backgroundColor
import com.tensionote.core.model.labelResId
import com.tensionote.core.model.regionalCategory
import com.tensionote.core.model.tintColor

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onRecordClick: (BloodPressureRecord) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .padding(20.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(stringResource(R.string.history_title), style = MaterialTheme.typography.headlineMedium)
        }

        if (state.records.isEmpty()) {
            item {
                Card {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(R.string.history_empty_title), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.history_empty_body), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        items(state.records) { record ->
            val category = record.regionalCategory
            Card(
                colors = CardDefaults.cardColors(containerColor = category.backgroundColor())
            ) {
                androidx.compose.material3.TextButton(
                    onClick = { onRecordClick(record) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 88.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(6.dp)
                                .padding(vertical = 10.dp)
                                .background(category.tintColor(), shape = MaterialTheme.shapes.small)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 14.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "${record.systolic}/${record.diastolic}",
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                RecordFormatters.formatMeasuredAt(record),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                stringResource(category.labelResId()),
                                style = MaterialTheme.typography.bodyMedium,
                                color = category.tintColor(),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
