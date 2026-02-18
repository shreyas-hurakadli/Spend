package com.example.spend

import co.yml.charts.common.extensions.roundTwoDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.Locale
import kotlin.math.abs

fun longToTime(longDate: Long): String {
    val dateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
    val date = Date(longDate)
    return dateFormat.format(date).substring(startIndex = 0, endIndex = 5)
}

/**
 * Calculates the time relative to a day
 */
fun longToDayTime(time: Long): String {
    val hours = time / 3600L
    val minutes = (time % 3600L) / 60L
    return String.format("%02d:%02d", hours, minutes)
}

/**
 * Returns the epoch second representing the start of the current day (midnight 0:00)
 * in the system default time zone and system time
 */
fun getTodayStart(): Long {
    val zoneId = ZoneId.systemDefault()
    return LocalDate.now(zoneId)
        .atStartOfDay(zoneId)
        .toEpochSecond()
}

/**
 * Returns the input epoch second in date format that is dd/MM/yyyy format in the system
 * default time zone
 */
fun longToDate(longDate: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    return dateFormat.format(longDate * 1000L)
}

/**
 * Returns the epoch second of the start of the first day of the
 * current month using system default time zone and system time
 */
fun getMonthStart(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.timeInMillis / 1000
}

/**
 * Returns the epoch second of the sunday in this week using system default time zone
 * and system time
 */
fun getSunday(): Long {
    val calendar = Calendar.getInstance()
    val currentDay = getTodayStart()

    calendar.timeInMillis = currentDay * 1000

    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    if (dayOfWeek == Calendar.SUNDAY)
        return currentDay

    val daysToSub = dayOfWeek - Calendar.SUNDAY
    calendar.add(Calendar.DAY_OF_YEAR, -daysToSub);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.timeInMillis / 1000
}

/**
 * Checks if the input string is a valid currency amount
 */
fun validateCurrency(input: String): Boolean {
    val regex = """^\+?\d+(\.\d\d?)?$""".toRegex()
    return regex.matches(input)
}

/**
 * Returns the local currency symbol
 */
fun getLocalCurrencySymbol(locale: Locale = Locale.getDefault()): String? =
    Currency.getInstance(locale).symbol

/**
 * Truncates the Double value to two decimal digits
 */
fun getFormattedAmount(value: Double): String = String.format(Locale.US, "%.2f", abs(value))

/**
 * Returns the string in the format "Month Date, Year"
 */
fun epochSecondsToDate(epochSeconds: Long): String {
    val dateTime =
        LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy")
    return dateTime.format(formatter)
}

/**
 * Converts the Double values to have a precision of two
 * Useful while handling currency
 */
fun Double.toTwoDecimal() = this.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()