package com.example.baro.core.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object DateTimeUtil {

    private val dateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val timeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("HH:mm")

    private val dateTimeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun parseDate(date: String): LocalDate =
        LocalDate.parse(date, dateFormatter)

    fun parseTime(time: String): LocalTime =
        LocalTime.parse(time, timeFormatter)

    fun formatDate(date: LocalDate): String =
        date.format(dateFormatter)

    fun formatTime(time: LocalTime): String =
        time.format(timeFormatter)

    fun formatDateTime(dateTime: LocalDateTime): String =
        dateTime.format(dateTimeFormatter)
}
