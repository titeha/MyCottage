package ru.mycottege.app.domain.timeline

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class TimelineRange(
  val start: LocalDate,
  val end: LocalDate
) {
  init {
    require(!end.isBefore(start)) { "Конец диапазона не может быть раньше начала" }
  }

  // Количество дней в диапазоне, включая границы.
  val daysCount: Int = (ChronoUnit.DAYS.between(start, end) + 1).toInt()
}

data class TimelineSegment(
  val offsetDays: Int,
  val lengthDays: Int
)

// Возвращает сегмент (смещение/длина) в пределах диапазона.
// endInclusive — включительно.
fun segmentFor(
  range: TimelineRange,
  start: LocalDate,
  endInclusive: LocalDate
): TimelineSegment? {
  if (endInclusive.isBefore(start)) return null

  val visibleStart = if (start.isBefore(range.start)) range.start else start
  val visibleEnd = if (endInclusive.isAfter(range.end)) range.end else endInclusive
  if (visibleEnd.isBefore(visibleStart)) return null

  val offset = ChronoUnit.DAYS.between(range.start, visibleStart).toInt()
  val length = (ChronoUnit.DAYS.between(visibleStart, visibleEnd) + 1).toInt()
  return TimelineSegment(offsetDays = offset, lengthDays = length)
}

fun offsetInRange(range: TimelineRange, date: LocalDate): Int? {
  if (date.isBefore(range.start) || date.isAfter(range.end)) return null
  return ChronoUnit.DAYS.between(range.start, date).toInt()
}
