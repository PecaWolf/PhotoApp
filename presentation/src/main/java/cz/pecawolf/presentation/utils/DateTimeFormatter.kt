package cz.pecawolf.presentation.utils

import java.text.DateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Instant

fun Instant.formatDateTimeBySystemLocale() = DateFormat.getDateTimeInstance(
    DateFormat.MEDIUM,
    DateFormat.MEDIUM,
    Locale.getDefault(),
).format(Date(toEpochMilliseconds()))

fun Instant.formatDateBySystemLocale() = DateFormat.getDateInstance(
    DateFormat.MEDIUM,
    Locale.getDefault(),
).format(Date(toEpochMilliseconds()))

fun Instant.formatTimeBySystemLocale() = DateFormat.getTimeInstance(
    DateFormat.MEDIUM,
    Locale.getDefault(),
).format(Date(toEpochMilliseconds()))
