package com.tensionote.core.rules

import com.tensionote.core.model.BloodPressureRecord
import com.tensionote.core.model.ReportSummary

class ReportSummaryBuilder {
    fun build(records: List<BloodPressureRecord>): ReportSummary {
        if (records.isEmpty()) return ReportSummary()

        return ReportSummary(
            count = records.size,
            averageSystolic = records.map { it.systolic }.average().toInt(),
            averageDiastolic = records.map { it.diastolic }.average().toInt(),
            averageHeartRate = records.map { it.heartRate }.average().toInt()
        )
    }
}
