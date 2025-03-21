package com.example.spend

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date

fun getTodayStart(): Long {
    val today = LocalDate.now()
    return today.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
}

fun LongToDate(longDate: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    return dateFormat.format(longDate)
}

fun getMonthStart(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.timeInMillis
}

fun getSunday(): Long {
    val calendar = Calendar.getInstance()
    val currentDay = getTodayStart()

    calendar.setTimeInMillis(currentDay)

    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    if (dayOfWeek == Calendar.SUNDAY)
        return currentDay

    val daysToSub = dayOfWeek - Calendar.SUNDAY
    calendar.add(Calendar.DAY_OF_YEAR, -daysToSub);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.timeInMillis
}