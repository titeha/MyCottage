package ru.mycottege.app.domain.events

object HarvestEventsGrouper {

  fun groupByDate(events: List<HarvestEvent>): List<HarvestEventsDay> {
    val grouped = events.groupBy { it.sortDate }

    return grouped
      .toSortedMap() // LocalDate сортируется естественным порядком
      .map { (date, list) ->
        val sortedInDay = list.sortedWith(
          compareBy<HarvestEvent> { it.status != HarvestEventStatus.ACTIVE } // ACTIVE выше
            .thenBy { it.cropId.name }
            .thenBy { it.plantingId }
        )
        HarvestEventsDay(date, sortedInDay)
      }
  }
}
