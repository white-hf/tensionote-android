package com.tensionote.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (
            intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            val reminders = ReminderStore(context.applicationContext).load()
            ReminderScheduler(context.applicationContext).sync(reminders)
        }
    }
}
