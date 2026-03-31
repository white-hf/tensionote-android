package com.tensionote.feature.trend

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.tensionote.core.model.BloodPressureRecord
import com.tensionote.core.model.BloodPressureStatus

@Composable
fun TrendChart(
    records: List<BloodPressureRecord>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        if (records.size < 2) return@Canvas

        val allValues = records.flatMap { listOf(it.systolic, it.diastolic) }
        val minValue = (allValues.minOrNull() ?: 60) - 10
        val maxValue = (allValues.maxOrNull() ?: 160) + 10
        val valueRange = (maxValue - minValue).coerceAtLeast(1)

        fun x(index: Int): Float {
            return (size.width / (records.size - 1).coerceAtLeast(1)) * index
        }

        fun y(value: Int): Float {
            val normalized = (value - minValue).toFloat() / valueRange.toFloat()
            return size.height - (normalized * size.height)
        }

        drawRect(
            color = Color(0xFFDCFCE7),
            topLeft = Offset(0f, y(120)),
            size = androidx.compose.ui.geometry.Size(size.width, y(60) - y(120))
        )

        val systolicPath = Path()
        val diastolicPath = Path()

        records.forEachIndexed { index, record ->
            val pointX = x(index)
            val systolicY = y(record.systolic)
            val diastolicY = y(record.diastolic)

            if (index == 0) {
                systolicPath.moveTo(pointX, systolicY)
                diastolicPath.moveTo(pointX, diastolicY)
            } else {
                systolicPath.lineTo(pointX, systolicY)
                diastolicPath.lineTo(pointX, diastolicY)
            }
        }

        drawPath(
            path = systolicPath,
            color = Color(0xFF2563EB),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
        drawPath(
            path = diastolicPath,
            color = Color(0xFF0F766E),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        records.forEachIndexed { index, record ->
            drawCircle(
                color = statusColor(record.status),
                radius = 7f,
                center = Offset(x(index), y(record.systolic))
            )
        }
    }
}

private fun statusColor(status: BloodPressureStatus): Color {
    return when (status) {
        BloodPressureStatus.NORMAL -> Color(0xFF16A34A)
        BloodPressureStatus.SYSTOLIC_HIGH -> Color(0xFFDC2626)
        BloodPressureStatus.DIASTOLIC_HIGH -> Color(0xFFEA580C)
        BloodPressureStatus.BOTH_HIGH -> Color(0xFFB91C1C)
        BloodPressureStatus.VARIABILITY -> Color(0xFF7C3AED)
    }
}
