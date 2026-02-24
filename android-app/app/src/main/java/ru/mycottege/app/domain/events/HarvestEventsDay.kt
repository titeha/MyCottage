package ru.mycottege.app.domain.events

import java.time.LocalDate

data class HarvestEventsDay(
  val date: LocalDate,
  val events: List<HarvestEvent>
)
