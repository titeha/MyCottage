package ru.mycottege.app.domain.harvest

import java.time.LocalDate

data class HarvestWindow(
  val start: LocalDate,
  val end: LocalDate
)
