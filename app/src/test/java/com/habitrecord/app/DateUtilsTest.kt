package com.habitrecord.app

import com.habitrecord.app.utils.DateUtils
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate

/**
 * 单元测试
 */
class DateUtilsTest {

    @Test
    fun testFormatAndParse() {
        val date = LocalDate.of(2025, 10, 26)
        val formatted = DateUtils.formatDate(date)
        assertEquals("2025-10-26", formatted)

        val parsed = DateUtils.parseDate(formatted)
        assertEquals(date, parsed)
    }

    @Test
    fun testGetMonthDays() {
        val days = DateUtils.getMonthDays(2025, 10)
        assertEquals(31, days.size)
        assertEquals(LocalDate.of(2025, 10, 1), days.first())
        assertEquals(LocalDate.of(2025, 10, 31), days.last())
    }
}

