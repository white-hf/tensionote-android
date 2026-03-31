package com.tensionote.core.rules

class RecordInputValidator {
    fun isValid(systolic: Int, diastolic: Int, heartRate: Int): Boolean {
        return systolic in 70..260 &&
            diastolic in 40..180 &&
            heartRate in 30..220
    }
}
