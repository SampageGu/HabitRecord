package com.habitrecord.app.utils

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * 日期工具类
 */
object DateUtils {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun today(): String {
        return LocalDate.now().format(dateFormatter)
    }

    fun formatDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }

    fun parseDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString, dateFormatter)
    }

    fun getMonthDays(year: Int, month: Int): List<LocalDate> {
        val yearMonth = YearMonth.of(year, month)
        val daysInMonth = yearMonth.lengthOfMonth()
        return (1..daysInMonth).map { day ->
            LocalDate.of(year, month, day)
        }
    }

    fun getYearMonths(year: Int): List<YearMonth> {
        return (1..12).map { month ->
            YearMonth.of(year, month)
        }
    }

    fun getCurrentYearMonth(): YearMonth {
        return YearMonth.now()
    }
}

