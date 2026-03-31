package com.tensionote.feature.trend

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tensionote.R
import com.tensionote.core.model.BloodPressureRecord
import com.tensionote.core.model.BloodPressureStatus
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@Composable
fun TrendChart(
    records: List<BloodPressureRecord>,
    modifier: Modifier = Modifier
) {
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val gridColor = Color(0xFFD7E7E5)
    val systolicColor = Color(0xFFE65555)
    val diastolicColor = Color(0xFF177E89)
    val systolicRangeColor = Color(0x1AE65555)
    val diastolicRangeColor = Color(0x1A177E89)
    val pressureAxisLabel = stringResource(R.string.trend_chart_axis_pressure)
    val dateAxisLabel = stringResource(R.string.trend_chart_axis_date)
    val systolicLegend = stringResource(R.string.trend_chart_legend_systolic)
    val diastolicLegend = stringResource(R.string.trend_chart_legend_diastolic)
    val systolicRangeLegend = stringResource(R.string.trend_chart_range_systolic_normal)
    val diastolicRangeLegend = stringResource(R.string.trend_chart_range_diastolic_normal)
    val locale = currentLocale()
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale)
    val density = LocalDensity.current
    val labelTextSize = with(density) { 12.sp.toPx() }
    val smallTextSize = with(density) { 10.sp.toPx() }

    val allValues = records.flatMap { listOf(it.systolic, it.diastolic) }
    val minData = allValues.minOrNull() ?: 60
    val maxData = allValues.maxOrNull() ?: 160
    val minValue = min(60, ((minData - 10) / 10) * 10)
    val maxValue = max(160, (((maxData + 9) / 10) * 10))
    val tickValues = listOf(maxValue, 140, 120, 90, 80, 60, minValue)
        .distinct()
        .filter { it in minValue..maxValue }
        .sortedDescending()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            if (records.isEmpty()) return@Canvas

            val leftPadding = 90f
            val rightPadding = 28f
            val topPadding = 28f
            val bottomPadding = 52f
            val chartWidth = size.width - leftPadding - rightPadding
            val chartHeight = size.height - topPadding - bottomPadding
            if (chartWidth <= 0f || chartHeight <= 0f) return@Canvas

            fun chartX(index: Int): Float {
                if (records.size == 1) return leftPadding + (chartWidth / 2f)
                return leftPadding + (chartWidth * index / (records.size - 1).coerceAtLeast(1))
            }

            fun chartY(value: Int): Float {
                val normalized = (value - minValue).toFloat() / (maxValue - minValue).coerceAtLeast(1).toFloat()
                return topPadding + chartHeight - (normalized * chartHeight)
            }

            drawRect(
                color = systolicRangeColor,
                topLeft = Offset(leftPadding, chartY(120)),
                size = Size(chartWidth, chartY(90) - chartY(120))
            )
            drawRect(
                color = diastolicRangeColor,
                topLeft = Offset(leftPadding, chartY(80)),
                size = Size(chartWidth, chartY(60) - chartY(80))
            )

            val axisPaint = Paint().apply {
                isAntiAlias = true
                color = labelColor.toArgb()
                textSize = labelTextSize
            }
            val smallPaint = Paint().apply {
                isAntiAlias = true
                color = labelColor.toArgb()
                textSize = smallTextSize
            }

            tickValues.forEach { tick ->
                val y = chartY(tick)
                drawLine(
                    color = if (tick == 140 || tick == 90) Color(0xFFB9CACA) else gridColor,
                    start = Offset(leftPadding, y),
                    end = Offset(leftPadding + chartWidth, y),
                    strokeWidth = if (tick == 140 || tick == 90) 2.5f else 1.5f
                )
                drawContext.canvas.nativeCanvas.drawText(
                    tick.toString(),
                    8f,
                    y + (labelTextSize / 3f),
                    axisPaint
                )
            }

            drawContext.canvas.nativeCanvas.drawText(
                pressureAxisLabel,
                8f,
                topPadding - 6f,
                smallPaint
            )

            val systolicPath = Path()
            val diastolicPath = Path()
            records.forEachIndexed { index, record ->
                val x = chartX(index)
                val systolicY = chartY(record.systolic)
                val diastolicY = chartY(record.diastolic)
                if (index == 0) {
                    systolicPath.moveTo(x, systolicY)
                    diastolicPath.moveTo(x, diastolicY)
                } else {
                    systolicPath.lineTo(x, systolicY)
                    diastolicPath.lineTo(x, diastolicY)
                }
            }

            drawPath(
                path = systolicPath,
                color = systolicColor,
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )
            drawPath(
                path = diastolicPath,
                color = diastolicColor,
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )

            records.forEachIndexed { index, record ->
                drawCircle(
                    color = statusColor(record.status),
                    radius = 7f,
                    center = Offset(chartX(index), chartY(record.systolic))
                )
                drawCircle(
                    color = diastolicColor,
                    radius = 5.5f,
                    center = Offset(chartX(index), chartY(record.diastolic))
                )
            }

            val labelIndices = listOf(0, records.lastIndex / 2, records.lastIndex).distinct()
            labelIndices.forEach { index ->
                val dateLabel = dateFormatter.format(records[index].measuredAt.atZone(ZoneId.systemDefault()))
                val textWidth = axisPaint.measureText(dateLabel)
                val x = chartX(index) - (textWidth / 2f)
                drawContext.canvas.nativeCanvas.drawText(
                    dateLabel,
                    x.coerceIn(leftPadding, leftPadding + chartWidth - textWidth),
                    topPadding + chartHeight + 34f,
                    axisPaint
                )
            }

            drawContext.canvas.nativeCanvas.drawText(
                dateAxisLabel,
                leftPadding + chartWidth - 36f,
                topPadding + chartHeight + 18f,
                smallPaint
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LegendItem(color = systolicColor, text = systolicLegend)
            LegendItem(color = diastolicColor, text = diastolicLegend)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LegendItem(color = systolicRangeColor, text = systolicRangeLegend)
            LegendItem(color = diastolicRangeColor, text = diastolicRangeLegend)
        }
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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

@Composable
private fun currentLocale(): Locale {
    val configuration = LocalConfiguration.current
    @Suppress("DEPRECATION")
    return configuration.locales[0] ?: Locale.getDefault()
}
