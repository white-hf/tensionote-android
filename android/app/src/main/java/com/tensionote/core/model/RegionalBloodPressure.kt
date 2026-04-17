package com.tensionote.core.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.tensionote.R
import java.util.Locale

enum class BloodPressureRegionalStandard {
    UNITED_STATES,
    EUROPE,
    UNITED_KINGDOM,
    JAPAN,
    WHO_DEFAULT;

    companion object {
        fun current(): BloodPressureRegionalStandard {
            return when (Locale.getDefault().country.uppercase(Locale.US)) {
                "US" -> UNITED_STATES
                "GB" -> UNITED_KINGDOM
                "JP" -> JAPAN
                "CN" -> WHO_DEFAULT
                "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR", "DE",
                "GR", "HU", "IE", "IT", "LV", "LT", "LU", "MT", "NL", "PL", "PT",
                "RO", "SK", "SI", "ES", "SE", "IS", "LI", "NO", "CH" -> EUROPE
                else -> WHO_DEFAULT
            }
        }
    }

    val hypertensionSystolicThreshold: Int
        get() = if (this == UNITED_STATES) 130 else 140

    val hypertensionDiastolicThreshold: Int
        get() = if (this == UNITED_STATES) 80 else 90

    fun isSystolicAboveHypertensionThreshold(systolic: Int): Boolean {
        return systolic >= hypertensionSystolicThreshold
    }

    fun isDiastolicAboveHypertensionThreshold(diastolic: Int): Boolean {
        return diastolic >= hypertensionDiastolicThreshold
    }
}

enum class BloodPressureCategory {
    NORMAL,
    HIGH_NORMAL,
    ELEVATED,
    STAGE_1_HYPERTENSION,
    STAGE_2_HYPERTENSION,
    HYPERTENSION,
    GRADE_1_HYPERTENSION,
    GRADE_2_HYPERTENSION,
    GRADE_3_HYPERTENSION,
    SEVERE_HYPERTENSION,
    HYPERTENSIVE_CRISIS
}

data class BloodPressureAssessment(
    val standard: BloodPressureRegionalStandard,
    val category: BloodPressureCategory
)

class RegionalBloodPressureEvaluator(
    val standard: BloodPressureRegionalStandard = BloodPressureRegionalStandard.current()
) {
    fun evaluate(systolic: Int, diastolic: Int): BloodPressureCategory {
        return when (standard) {
            BloodPressureRegionalStandard.UNITED_STATES -> when {
                systolic >= 180 || diastolic >= 120 -> BloodPressureCategory.HYPERTENSIVE_CRISIS
                systolic >= 140 || diastolic >= 90 -> BloodPressureCategory.STAGE_2_HYPERTENSION
                systolic >= 130 || diastolic >= 80 -> BloodPressureCategory.STAGE_1_HYPERTENSION
                systolic in 120..129 && diastolic < 80 -> BloodPressureCategory.ELEVATED
                else -> BloodPressureCategory.NORMAL
            }

            BloodPressureRegionalStandard.EUROPE -> when {
                systolic >= 180 || diastolic >= 120 -> BloodPressureCategory.HYPERTENSIVE_CRISIS
                systolic >= 140 || diastolic >= 90 -> BloodPressureCategory.HYPERTENSION
                systolic < 120 && diastolic < 70 -> BloodPressureCategory.NORMAL
                else -> BloodPressureCategory.ELEVATED
            }

            BloodPressureRegionalStandard.UNITED_KINGDOM -> when {
                systolic >= 180 || diastolic >= 120 -> BloodPressureCategory.SEVERE_HYPERTENSION
                systolic >= 160 || diastolic >= 100 -> BloodPressureCategory.STAGE_2_HYPERTENSION
                systolic >= 140 || diastolic >= 90 -> BloodPressureCategory.STAGE_1_HYPERTENSION
                else -> BloodPressureCategory.NORMAL
            }

            BloodPressureRegionalStandard.JAPAN -> when {
                systolic >= 180 || diastolic >= 110 -> BloodPressureCategory.GRADE_3_HYPERTENSION
                systolic >= 160 || diastolic >= 100 -> BloodPressureCategory.GRADE_2_HYPERTENSION
                systolic >= 140 || diastolic >= 90 -> BloodPressureCategory.GRADE_1_HYPERTENSION
                systolic >= 130 || diastolic >= 80 -> BloodPressureCategory.ELEVATED
                systolic in 120..129 && diastolic < 80 -> BloodPressureCategory.HIGH_NORMAL
                else -> BloodPressureCategory.NORMAL
            }

            BloodPressureRegionalStandard.WHO_DEFAULT -> when {
                systolic >= 180 || diastolic >= 110 -> BloodPressureCategory.GRADE_3_HYPERTENSION
                systolic >= 160 || diastolic >= 100 -> BloodPressureCategory.GRADE_2_HYPERTENSION
                systolic >= 140 || diastolic >= 90 -> BloodPressureCategory.GRADE_1_HYPERTENSION
                else -> BloodPressureCategory.NORMAL
            }
        }
    }

    fun assess(systolic: Int, diastolic: Int): BloodPressureAssessment {
        return BloodPressureAssessment(
            standard = standard,
            category = evaluate(systolic, diastolic)
        )
    }
}

val BloodPressureRecord.regionalAssessment: BloodPressureAssessment
    get() = RegionalBloodPressureEvaluator().assess(systolic, diastolic)

val BloodPressureRecord.regionalCategory: BloodPressureCategory
    get() = regionalAssessment.category

@StringRes
fun BloodPressureCategory.labelResId(): Int {
    return when (this) {
        BloodPressureCategory.NORMAL -> R.string.blood_pressure_category_normal
        BloodPressureCategory.HIGH_NORMAL -> R.string.blood_pressure_category_high_normal
        BloodPressureCategory.ELEVATED -> R.string.blood_pressure_category_elevated
        BloodPressureCategory.STAGE_1_HYPERTENSION -> R.string.blood_pressure_category_stage_1_hypertension
        BloodPressureCategory.STAGE_2_HYPERTENSION -> R.string.blood_pressure_category_stage_2_hypertension
        BloodPressureCategory.HYPERTENSION -> R.string.blood_pressure_category_hypertension
        BloodPressureCategory.GRADE_1_HYPERTENSION -> R.string.blood_pressure_category_grade_1_hypertension
        BloodPressureCategory.GRADE_2_HYPERTENSION -> R.string.blood_pressure_category_grade_2_hypertension
        BloodPressureCategory.GRADE_3_HYPERTENSION -> R.string.blood_pressure_category_grade_3_hypertension
        BloodPressureCategory.SEVERE_HYPERTENSION -> R.string.blood_pressure_category_severe_hypertension
        BloodPressureCategory.HYPERTENSIVE_CRISIS -> R.string.blood_pressure_category_hypertensive_crisis
    }
}

fun BloodPressureCategory.tintColor(): Color {
    return when (this) {
        BloodPressureCategory.NORMAL -> Color(0xFF2E7D32)
        BloodPressureCategory.HIGH_NORMAL,
        BloodPressureCategory.ELEVATED -> Color(0xFFC18A00)
        BloodPressureCategory.STAGE_1_HYPERTENSION,
        BloodPressureCategory.GRADE_1_HYPERTENSION -> Color(0xFFEF6C00)
        BloodPressureCategory.STAGE_2_HYPERTENSION,
        BloodPressureCategory.HYPERTENSION,
        BloodPressureCategory.GRADE_2_HYPERTENSION,
        BloodPressureCategory.SEVERE_HYPERTENSION -> Color(0xFFC62828)
        BloodPressureCategory.GRADE_3_HYPERTENSION,
        BloodPressureCategory.HYPERTENSIVE_CRISIS -> Color(0xFF8E1B1B)
    }
}

fun BloodPressureCategory.backgroundColor(): Color {
    return when (this) {
        BloodPressureCategory.NORMAL -> Color(0xFFEAF6EC)
        BloodPressureCategory.HIGH_NORMAL,
        BloodPressureCategory.ELEVATED -> Color(0xFFFFF4D6)
        BloodPressureCategory.STAGE_1_HYPERTENSION,
        BloodPressureCategory.GRADE_1_HYPERTENSION -> Color(0xFFFFE6D2)
        BloodPressureCategory.STAGE_2_HYPERTENSION,
        BloodPressureCategory.HYPERTENSION,
        BloodPressureCategory.GRADE_2_HYPERTENSION,
        BloodPressureCategory.SEVERE_HYPERTENSION -> Color(0xFFF8D7DA)
        BloodPressureCategory.GRADE_3_HYPERTENSION,
        BloodPressureCategory.HYPERTENSIVE_CRISIS -> Color(0xFFF1C4C9)
    }
}
