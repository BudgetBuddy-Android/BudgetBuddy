package com.gdr.budgetbuddy.utils

import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CommonUtil {

    private const val DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_PATTERN = "yyyy-MM-dd"

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat(DATE_TIME_PATTERN, Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

}