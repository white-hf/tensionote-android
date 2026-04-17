package com.tensionote.core.export

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ClipData
import android.content.Intent
import com.tensionote.R
import androidx.core.content.FileProvider
import java.io.File

object ReportShareHelper {
    fun sharePdf(
        context: Context,
        file: File,
        subject: String,
        body: String
    ): Boolean {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            clipData = ClipData.newRawUri(file.name, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(
                Intent.createChooser(
                    fallbackIntent,
                    context.getString(R.string.report_share_chooser_title)
                )
            )
            return true
        } catch (_: ActivityNotFoundException) {
        }

        return false
    }
}
