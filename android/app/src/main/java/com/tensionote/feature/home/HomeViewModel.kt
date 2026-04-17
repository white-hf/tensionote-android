package com.tensionote.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tensionote.core.model.BloodPressureRecord
import com.tensionote.core.model.BloodPressureCategory
import com.tensionote.core.model.RegionalBloodPressureEvaluator
import com.tensionote.core.model.regionalCategory
import com.tensionote.core.repository.AppGraph
import com.tensionote.core.repository.BloodPressureRepository
import com.tensionote.core.rules.RecordInputValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val systolicInput: String = "",
    val diastolicInput: String = "",
    val heartRateInput: String = "",
    val trendRecords: List<BloodPressureRecord> = emptyList(),
    val selectedCategory: BloodPressureCategory = BloodPressureCategory.NORMAL,
    val draftCategory: BloodPressureCategory? = null,
    val validationMessageKey: String? = null
)

class HomeViewModel(
    private val repository: BloodPressureRepository = AppGraph.bloodPressureRepository,
    private val evaluator: RegionalBloodPressureEvaluator = RegionalBloodPressureEvaluator(),
    private val validator: RecordInputValidator = RecordInputValidator()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(trendRecords = repository.fetchRecentTwoWeeks())
    )
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.observeAll().collect {
                val trendRecords = repository.fetchRecentTwoWeeks()
                _uiState.update { state ->
                    state.copy(
                        trendRecords = trendRecords,
                        selectedCategory = trendRecords.lastOrNull()?.regionalCategory ?: BloodPressureCategory.NORMAL
                    )
                }
            }
        }
    }

    fun updateSystolic(value: String) = _uiState.update { state ->
        val updated = state.copy(
            systolicInput = value,
            validationMessageKey = null
        )
        updated.copy(draftCategory = evaluateDraftCategory(updated.systolicInput, updated.diastolicInput))
    }

    fun updateDiastolic(value: String) = _uiState.update { state ->
        val updated = state.copy(
            diastolicInput = value,
            validationMessageKey = null
        )
        updated.copy(draftCategory = evaluateDraftCategory(updated.systolicInput, updated.diastolicInput))
    }

    fun updateHeartRate(value: String) = _uiState.update {
        it.copy(heartRateInput = value, validationMessageKey = null)
    }

    fun saveQuickRecord() {
        val state = _uiState.value
        val systolic = state.systolicInput.toIntOrNull()
        val diastolic = state.diastolicInput.toIntOrNull()
        val heartRate = state.heartRateInput.toIntOrNull()

        if (systolic == null || diastolic == null || heartRate == null) {
            _uiState.update { it.copy(validationMessageKey = "validation_enter_valid_numbers") }
            return
        }

        if (!validator.isValid(systolic, diastolic, heartRate)) {
            _uiState.update { it.copy(validationMessageKey = "validation_out_of_range") }
            return
        }

        val record = repository.saveQuickRecord(systolic, diastolic, heartRate)
        _uiState.update {
            it.copy(
                systolicInput = "",
                diastolicInput = "",
                heartRateInput = "",
                trendRecords = repository.fetchRecentTwoWeeks(),
                selectedCategory = record.regionalCategory,
                draftCategory = null,
                validationMessageKey = null
            )
        }
    }

    fun selectRecord(record: BloodPressureRecord) {
        _uiState.update { it.copy(selectedCategory = record.regionalCategory) }
    }

    fun currentDraftCategory(): BloodPressureCategory {
        return _uiState.value.draftCategory ?: BloodPressureCategory.NORMAL
    }

    private fun evaluateDraftCategory(systolicInput: String, diastolicInput: String): BloodPressureCategory? {
        val systolic = systolicInput.toIntOrNull()
        val diastolic = diastolicInput.toIntOrNull()
        return if (systolic == null || diastolic == null) {
            null
        } else {
            evaluator.evaluate(systolic, diastolic)
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel() as T
                }
            }
        }
    }
}
