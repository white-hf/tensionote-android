package com.tensionote.feature.report

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tensionote.R
import com.tensionote.core.model.BloodPressureRecord
import com.tensionote.core.model.ReportSummary
import com.tensionote.core.export.PdfReportExporter
import com.tensionote.core.repository.AppGraph
import com.tensionote.core.repository.BloodPressureRepository
import com.tensionote.core.rules.ReportSummaryBuilder
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportUiState(
    val selectedDays: Int = 30,
    val isCustomRange: Boolean = false,
    val startDate: LocalDate = LocalDate.now().minusDays(29),
    val endDate: LocalDate = LocalDate.now(),
    val records: List<BloodPressureRecord> = emptyList(),
    val summary: ReportSummary = ReportSummary(),
    val exportedFileName: String? = null,
    val exportedFilePath: String? = null
)

class ReportViewModel(
    private val context: Context,
    private val repository: BloodPressureRepository = AppGraph.bloodPressureRepository,
    private val builder: ReportSummaryBuilder = ReportSummaryBuilder()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState
    private val exporter = PdfReportExporter(context.applicationContext)

    init {
        viewModelScope.launch {
            repository.observeAll().collect {
                reload()
            }
        }
        reload()
    }

    fun selectDays(days: Int) {
        _uiState.update {
            it.copy(
                selectedDays = days,
                isCustomRange = false,
                startDate = LocalDate.now().minusDays(days.toLong() - 1),
                endDate = LocalDate.now()
            )
        }
        reload()
    }

    fun selectCustomRange() {
        _uiState.update {
            it.copy(
                isCustomRange = true,
                startDate = it.startDate,
                endDate = if (it.endDate.isBefore(it.startDate)) it.startDate else it.endDate
            )
        }
        reload()
    }

    fun updateStartDate(date: LocalDate) {
        _uiState.update {
            val endDate = if (it.endDate.isBefore(date)) date else it.endDate
            it.copy(
                isCustomRange = true,
                startDate = date,
                endDate = endDate
            )
        }
        reload()
    }

    fun updateEndDate(date: LocalDate) {
        _uiState.update {
            val startDate = if (date.isBefore(it.startDate)) date else it.startDate
            it.copy(
                isCustomRange = true,
                startDate = startDate,
                endDate = date
            )
        }
        reload()
    }

    private fun reload() {
        val state = _uiState.value
        val records = if (state.isCustomRange) {
            repository.fetchRecords(state.startDate, state.endDate)
        } else {
            repository.fetchRecords(state.selectedDays)
        }
        _uiState.update {
            it.copy(
                records = records,
                summary = builder.build(records),
                exportedFileName = null,
                exportedFilePath = null
            )
        }
    }

    fun exportPdf(): String? {
        val state = _uiState.value
        val file = exporter.export(state.summary, state.records, state.rangeDays())
        _uiState.update {
            it.copy(
                exportedFileName = file?.name,
                exportedFilePath = file?.absolutePath
            )
        }
        return file?.absolutePath
    }

    fun emailSubject(): String {
        return context.getString(R.string.report_email_subject_format, _uiState.value.rangeDays())
    }

    fun emailBody(): String {
        val state = _uiState.value
        return context.getString(
            R.string.report_email_body_format,
            state.rangeDays(),
            state.summary.count,
            state.summary.averageSystolic,
            state.summary.averageDiastolic,
            state.summary.averageHeartRate
        )
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ReportViewModel(context.applicationContext) as T
                }
            }
        }
    }
}

private fun ReportUiState.rangeDays(): Int {
    return if (isCustomRange) {
        (ChronoUnit.DAYS.between(startDate, endDate) + 1).toInt()
    } else {
        selectedDays
    }
}
