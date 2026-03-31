package com.tensionote.feature.reminder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tensionote.core.notification.ReminderScheduler
import com.tensionote.core.notification.ReminderStore
import com.tensionote.core.model.ReminderItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ReminderUiState(
    val reminders: List<ReminderItem> = emptyList()
)

class ReminderViewModel(
    private val context: Context
) : ViewModel() {
    private val scheduler = ReminderScheduler(context.applicationContext)
    private val store = ReminderStore(context.applicationContext)
    private val _uiState = MutableStateFlow(ReminderUiState(reminders = store.load()))
    val uiState: StateFlow<ReminderUiState> = _uiState

    init {
        scheduler.sync(_uiState.value.reminders)
    }

    fun addReminder() {
        _uiState.update { state ->
            state.copy(reminders = sortReminders(state.reminders + ReminderItem(hour = 9, minute = 0)))
        }
        persist()
        scheduler.sync(_uiState.value.reminders)
    }

    fun toggleReminder(id: String) {
        _uiState.update { state ->
            state.copy(
                reminders = sortReminders(state.reminders.map {
                    if (it.id == id) it.copy(enabled = !it.enabled) else it
                })
            )
        }
        persist()
        scheduler.sync(_uiState.value.reminders)
    }

    fun updateReminderTime(id: String, hour: Int, minute: Int) {
        _uiState.update { state ->
            state.copy(
                reminders = sortReminders(state.reminders.map {
                    if (it.id == id) it.copy(hour = hour, minute = minute) else it
                })
            )
        }
        persist()
        scheduler.sync(_uiState.value.reminders)
    }

    fun deleteReminder(id: String) {
        _uiState.update { state ->
            state.copy(reminders = sortReminders(state.reminders.filterNot { it.id == id }))
        }
        persist()
        scheduler.sync(_uiState.value.reminders)
    }

    private fun persist(source: List<ReminderItem> = _uiState.value.reminders) {
        store.persist(source)
    }

    private fun sortReminders(source: List<ReminderItem>): List<ReminderItem> {
        return source.sortedWith(compareBy<ReminderItem> { it.hour }.thenBy { it.minute })
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ReminderViewModel(context.applicationContext) as T
                }
            }
        }
    }
}
