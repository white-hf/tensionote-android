package com.tensionote.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.tensionote.core.model.ReminderItem
import java.util.Calendar

class ReminderScheduler(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun sync(reminders: List<ReminderItem>) {
        reminders.forEach { reminder ->
            val pendingIntent = pendingIntent(reminder)
            alarmManager.cancel(pendingIntent)

            if (!reminder.enabled) return@forEach

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, reminder.hour)
                set(Calendar.MINUTE, reminder.minute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    private fun pendingIntent(reminder: ReminderItem): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).putExtra("reminder_id", reminder.id)
        return PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
