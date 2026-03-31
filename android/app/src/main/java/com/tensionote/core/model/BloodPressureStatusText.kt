package com.tensionote.core.model

import androidx.annotation.StringRes
import com.tensionote.R

@StringRes
fun BloodPressureStatus.labelResId(): Int {
    return when (this) {
        BloodPressureStatus.NORMAL -> R.string.blood_pressure_status_normal
        BloodPressureStatus.SYSTOLIC_HIGH -> R.string.blood_pressure_status_systolic_high
        BloodPressureStatus.DIASTOLIC_HIGH -> R.string.blood_pressure_status_diastolic_high
        BloodPressureStatus.BOTH_HIGH -> R.string.blood_pressure_status_both_high
        BloodPressureStatus.VARIABILITY -> R.string.blood_pressure_status_variability
    }
}
