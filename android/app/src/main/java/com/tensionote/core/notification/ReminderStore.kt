package com.tensionote.core.notification

import android.content.Context
import com.tensionote.core.model.ReminderItem
import org.json.JSONArray
import org.json.JSONObject

class ReminderStore(
    private val context: Context
) {
    private val fileName = "tensionote_reminders.json"

    fun load(): List<ReminderItem> {
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
                        ReminderItem(
                            id = item.getString("id"),
                            hour = item.getInt("hour"),
                            minute = item.getInt("minute"),
                            enabled = item.getBoolean("enabled")
                        )
                    )
                }
            }.sortedWith(compareBy<ReminderItem> { it.hour }.thenBy { it.minute })
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun persist(reminders: List<ReminderItem>) {
        val json = JSONArray()
        reminders.forEach { reminder ->
            json.put(
                JSONObject()
                    .put("id", reminder.id)
                    .put("hour", reminder.hour)
                    .put("minute", reminder.minute)
                    .put("enabled", reminder.enabled)
            )
        }
        context.filesDir.resolve(fileName).writeText(json.toString())
    }
}
