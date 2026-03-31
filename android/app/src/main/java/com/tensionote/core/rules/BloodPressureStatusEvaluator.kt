package com.tensionote.core.rules

import com.tensionote.core.model.BloodPressureStatus

class BloodPressureStatusEvaluator {
    fun evaluate(systolic: Int, diastolic: Int): BloodPressureStatus {
        val systolicHigh = systolic >= 140
        val diastolicHigh = diastolic >= 90

        return when {
            systolicHigh && diastolicHigh -> BloodPressureStatus.BOTH_HIGH
            systolicHigh -> BloodPressureStatus.SYSTOLIC_HIGH
            diastolicHigh -> BloodPressureStatus.DIASTOLIC_HIGH
            else -> BloodPressureStatus.NORMAL
        }
    }
}
