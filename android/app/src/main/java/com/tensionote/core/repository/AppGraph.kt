package com.tensionote.core.repository

import android.content.Context

object AppGraph {
    lateinit var bloodPressureRepository: BloodPressureRepository
        private set

    fun initialize(context: Context) {
        if (!::bloodPressureRepository.isInitialized) {
            bloodPressureRepository = InMemoryBloodPressureRepository(context.applicationContext)
        }
    }
}
