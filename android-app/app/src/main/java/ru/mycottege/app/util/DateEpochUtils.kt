package ru.mycottege.app.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private val UTC: ZoneId = ZoneId.of("UTC")

fun localDateToUtcEpochMillis(date: LocalDate): Long =
  date.atStartOfDay(UTC).toInstant().toEpochMilli()

fun utcEpochMillisToLocalDate(millis: Long): LocalDate =
  Instant.ofEpochMilli(millis).atZone(UTC).toLocalDate()
