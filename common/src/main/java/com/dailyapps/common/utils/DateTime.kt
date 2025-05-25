package com.dailyapps.common.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale
import java.util.TimeZone

@Suppress("UtilityClassWithPublicConstructor")
class DateTime {
    companion object {
        const val DATE_FORMAT = "dd MMM yyyy"

        fun getIndoDayOfWeek(date: String): String {
            return try {
                when (LocalDate.parse(date).dayOfWeek.value) {
                    1 -> "Senin"
                    2 -> "Selasa"
                    3 -> "Rabu"
                    4 -> "Kamis"
                    5 -> "Jumat"
                    6 -> "Sabtu"
                    7 -> "Minggu"
                    else -> ""
                }
            } catch (e: Exception) {
                ""
            }
        }

        @SuppressLint("WeekBasedYear")
        fun convertToShort(
            date: String,
            pattern: String = "dd LLL YYYY",
            default: String = ""
        ): String {
            return try {
                val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                val localDate = formatter.parse(date)
                formatter.format(localDate)
            } catch (e: java.lang.Exception) {
                date
            }
        }

        @SuppressLint("WeekBasedYear")
        fun formatDate(
            date: String,
            pattern: String = "dd LLL YYYY",
            default: String = ""
        ): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val dateFormat = inputFormat.parse(date)
                val outputFormat = SimpleDateFormat(pattern, Locale.getDefault())
                outputFormat.format(dateFormat!!)
            } catch (e: Exception) {
                // Fallback to existing conversion if format doesn't match
                DateTime.convertToShort(date)
            }
        }
    }
}