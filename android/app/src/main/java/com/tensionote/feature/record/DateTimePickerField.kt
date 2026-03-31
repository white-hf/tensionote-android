package com.tensionote.feature.record

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tensionote.core.model.RecordFormatters
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun DateTimePickerField(
    measuredAt: Instant,
    onChanged: (Instant) -> Unit,
    label: String
) {
    val context = LocalContext.current
    val dateTime = LocalDateTime.ofInstant(measuredAt, ZoneId.systemDefault())

    Button(
        onClick = {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            val selected = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                            onChanged(selected)
                        },
                        dateTime.hour,
                        dateTime.minute,
                        DateFormat.is24HourFormat(context)
                    ).show()
                },
                dateTime.year,
                dateTime.monthValue - 1,
                dateTime.dayOfMonth
            ).show()
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(RecordFormatters.formatInstant(measuredAt), style = MaterialTheme.typography.titleMedium)
        }
    }
}
