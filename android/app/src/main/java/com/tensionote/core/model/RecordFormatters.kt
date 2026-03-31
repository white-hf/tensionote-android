package com.tensionote.core.model

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

object RecordFormatters {
    private fun formatter(): DateTimeFormatter {
        return DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())
    }

    fun formatMeasuredAt(record: BloodPressureRecord): String {
        return formatter().format(record.measuredAt)
    }

    fun formatInstant(instant: java.time.Instant): String {
        return formatter().format(instant)
    }
}
