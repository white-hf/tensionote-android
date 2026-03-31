package com.tensionote.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tensionote.core.model.BloodPressureRecord
import com.tensionote.core.repository.AppGraph
import com.tensionote.core.repository.BloodPressureRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HistoryUiState(
    val records: List<BloodPressureRecord> = emptyList()
)

class HistoryViewModel(
    repository: BloodPressureRepository = AppGraph.bloodPressureRepository
) : ViewModel() {
    val uiState: StateFlow<HistoryUiState> = repository.observeAll()
        .map { HistoryUiState(records = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HistoryUiState(records = repository.fetchAll())
        )

    companion object {
        fun factory(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HistoryViewModel() as T
                }
            }
        }
    }
}
