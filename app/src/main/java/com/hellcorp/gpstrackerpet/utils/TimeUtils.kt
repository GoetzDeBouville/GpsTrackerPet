package com.hellcorp.gpstrackerpet.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

object TimeUtils {
    @SuppressLint("SimpleDateFormat")
    private val timeFormatter = SimpleDateFormat("HH:mm:ss")

    fun getTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        timeFormatter.timeZone = TimeZone.getTimeZone("UTC") // Форматирует начало отчета в 00:00:00
        calendar.timeInMillis = timestamp
        return timeFormatter.format(calendar.time)
    }
}