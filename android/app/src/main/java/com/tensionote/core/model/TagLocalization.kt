package com.tensionote.core.model

import android.content.Context
import com.tensionote.R

object TagLocalization {
    fun localize(context: Context, tags: List<String>): String {
        return tags.joinToString(", ") { tag ->
            when (tag) {
                "tag_morning" -> context.getString(R.string.tag_morning)
                "tag_afternoon" -> context.getString(R.string.tag_afternoon)
                "tag_evening" -> context.getString(R.string.tag_evening)
                "tag_after_meal" -> context.getString(R.string.tag_after_meal)
                "tag_after_exercise" -> context.getString(R.string.tag_after_exercise)
                "tag_after_medication" -> context.getString(R.string.tag_after_medication)
                "tag_discomfort" -> context.getString(R.string.tag_discomfort)
                else -> tag
            }
        }
    }
}
