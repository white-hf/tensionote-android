package com.tensionote.core.model

import java.time.Instant
import java.util.UUID

data class BloodPressureRecord(
    val id: String = UUID.randomUUID().toString(),
    val systolic: Int,
    val diastolic: Int,
    val heartRate: Int,
    val measuredAt: Instant = Instant.now(),
    val tags: List<String> = emptyList(),
    val note: String? = null,
    val status: BloodPressureStatus
)

enum class BloodPressureStatus {
    NORMAL,
    SYSTOLIC_HIGH,
    DIASTOLIC_HIGH,
    BOTH_HIGH,
    VARIABILITY
}
