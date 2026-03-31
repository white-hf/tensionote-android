package com.tensionote.core.repository

import android.content.Context
import com.tensionote.core.model.BloodPressureRecord
import com.tensionote.core.rules.BloodPressureStatusEvaluator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject

interface BloodPressureRepository {
    fun saveQuickRecord(systolic: Int, diastolic: Int, heartRate: Int): BloodPressureRecord
    fun saveDetailedRecord(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        measuredAt: Instant,
        tags: List<String>,
        note: String?
    ): BloodPressureRecord
    fun fetchRecentTwoWeeks(): List<BloodPressureRecord>
    fun fetchAll(): List<BloodPressureRecord>
    fun fetchRecords(days: Int): List<BloodPressureRecord>
    fun fetchRecords(startDate: LocalDate, endDate: LocalDate): List<BloodPressureRecord>
    fun observeAll(): StateFlow<List<BloodPressureRecord>>
    fun deleteRecord(id: String)
    fun updateRecord(
        id: String,
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        measuredAt: Instant,
        tags: List<String>,
        note: String?
    )
}

class InMemoryBloodPressureRepository(
    private val context: Context
) : BloodPressureRepository {
    private val evaluator = BloodPressureStatusEvaluator()
    private val fileName = "tensionote_records.json"
    private val zoneId = ZoneId.systemDefault()

    private val records = loadRecords().toMutableList()
    private val recordsFlow = MutableStateFlow(records.sortedByDescending { it.measuredAt })

    override fun saveQuickRecord(systolic: Int, diastolic: Int, heartRate: Int): BloodPressureRecord {
        val record = BloodPressureRecord(
            systolic = systolic,
            diastolic = diastolic,
            heartRate = heartRate,
            status = evaluator.evaluate(systolic, diastolic)
        )
        records.add(record)
        persist()
        recordsFlow.value = fetchAll()
        return record
    }

    override fun saveDetailedRecord(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        measuredAt: Instant,
        tags: List<String>,
        note: String?
    ): BloodPressureRecord {
        val record = BloodPressureRecord(
            systolic = systolic,
            diastolic = diastolic,
            heartRate = heartRate,
            measuredAt = measuredAt,
            tags = tags,
            note = note,
            status = evaluator.evaluate(systolic, diastolic)
        )
        records.add(record)
        persist()
        recordsFlow.value = fetchAll()
        return record
    }

    override fun fetchRecentTwoWeeks(): List<BloodPressureRecord> {
        val cutoff = Instant.now().minus(14, ChronoUnit.DAYS)
        return records.filter { it.measuredAt.isAfter(cutoff) }.sortedBy { it.measuredAt }
    }

    override fun fetchAll(): List<BloodPressureRecord> {
        return records.sortedByDescending { it.measuredAt }
    }

    override fun fetchRecords(days: Int): List<BloodPressureRecord> {
        val cutoff = Instant.now().minus(days.toLong(), ChronoUnit.DAYS)
        return records.filter { it.measuredAt.isAfter(cutoff) }.sortedByDescending { it.measuredAt }
    }

    override fun fetchRecords(startDate: LocalDate, endDate: LocalDate): List<BloodPressureRecord> {
        val startInstant = startDate.atStartOfDay(zoneId).toInstant()
        val endExclusive = endDate.plusDays(1).atStartOfDay(zoneId).toInstant()
        return records
            .filter { it.measuredAt >= startInstant && it.measuredAt < endExclusive }
            .sortedByDescending { it.measuredAt }
    }

    override fun observeAll(): StateFlow<List<BloodPressureRecord>> = recordsFlow

    override fun deleteRecord(id: String) {
        records.removeAll { it.id == id }
        persist()
        recordsFlow.value = fetchAll()
    }

    override fun updateRecord(
        id: String,
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        measuredAt: Instant,
        tags: List<String>,
        note: String?
    ) {
        val index = records.indexOfFirst { it.id == id }
        if (index == -1) return
        records[index] = records[index].copy(
            systolic = systolic,
            diastolic = diastolic,
            heartRate = heartRate,
            measuredAt = measuredAt,
            tags = tags,
            note = note,
            status = evaluator.evaluate(systolic, diastolic)
        )
        persist()
        recordsFlow.value = fetchAll()
    }

    private fun loadRecords(): List<BloodPressureRecord> {
        val file = context.filesDir.resolve(fileName)
        if (!file.exists()) {
            return emptyList()
        }

        return try {
            val json = JSONArray(file.readText())
            buildList {
                for (index in 0 until json.length()) {
                    val item = json.getJSONObject(index)
                    add(
                        BloodPressureRecord(
                            id = item.getString("id"),
                            systolic = item.getInt("systolic"),
                            diastolic = item.getInt("diastolic"),
                            heartRate = item.getInt("heartRate"),
                            measuredAt = Instant.parse(item.getString("measuredAt")),
                            tags = item.optJSONArray("tags")?.let { tagsArray ->
                                buildList {
                                    for (tagIndex in 0 until tagsArray.length()) {
                                        add(tagsArray.getString(tagIndex))
                                    }
                                }
                            } ?: emptyList(),
                            note = item.optString("note").takeIf { it.isNotBlank() },
                            status = evaluator.evaluate(
                                systolic = item.getInt("systolic"),
                                diastolic = item.getInt("diastolic")
                            )
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun persist() {
        persist(records)
    }

    private fun persist(source: List<BloodPressureRecord>) {
        val json = JSONArray()
        source.forEach { record ->
            json.put(
                JSONObject()
                    .put("id", record.id)
                    .put("systolic", record.systolic)
                    .put("diastolic", record.diastolic)
                    .put("heartRate", record.heartRate)
                    .put("measuredAt", record.measuredAt.toString())
                    .put("tags", JSONArray(record.tags))
                    .put("note", record.note ?: "")
            )
        }
        context.filesDir.resolve(fileName).writeText(json.toString())
    }
}
