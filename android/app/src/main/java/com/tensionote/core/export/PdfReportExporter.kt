package com.tensionote.core.export

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.tensionote.R
import com.tensionote.core.model.BloodPressureRecord
import com.tensionote.core.model.RecordFormatters
import com.tensionote.core.model.ReportSummary
import com.tensionote.core.model.labelResId
import java.io.File

class PdfReportExporter(
    private val context: Context
) {
    fun export(summary: ReportSummary, records: List<BloodPressureRecord>, days: Int): File? {
        if (records.isEmpty()) {
            return null
        }

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val titlePaint = Paint().apply { textSize = 22f; isFakeBoldText = true }
        val bodyPaint = Paint().apply { textSize = 14f }

        canvas.drawText(context.getString(R.string.pdf_report_title), 32f, 40f, titlePaint)
        canvas.drawText("${context.getString(R.string.pdf_report_range_label)}: $days", 32f, 70f, bodyPaint)
        canvas.drawText("${context.getString(R.string.report_metric_count)}: ${summary.count}", 32f, 95f, bodyPaint)
        canvas.drawText("${context.getString(R.string.pdf_report_average_label)}: ${summary.averageSystolic}/${summary.averageDiastolic}", 32f, 120f, bodyPaint)
        canvas.drawText("${context.getString(R.string.report_metric_average_heart_rate)}: ${summary.averageHeartRate}", 32f, 145f, bodyPaint)

        var y = 190f
        records.take(12).forEach { record ->
            canvas.drawText(
                "${RecordFormatters.formatMeasuredAt(record)}  ${record.systolic}/${record.diastolic}  ${record.heartRate}  ${context.getString(record.status.labelResId())}",
                32f,
                y,
                bodyPaint
            )
            y += 22f
        }

        document.finishPage(page)

        return try {
            val file = File(context.cacheDir, "tensionote-report-$days.pdf")
            file.outputStream().use { output ->
                document.writeTo(output)
            }
            document.close()
            file
        } catch (_: Exception) {
            document.close()
            null
        }
    }
}
