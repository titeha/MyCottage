package ru.mycottege.app.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.mycottege.app.R
import ru.mycottege.app.data.local.db.DbProvider
import ru.mycottege.app.data.local.db.toSnapshot
import ru.mycottege.app.domain.events.HarvestEvent
import ru.mycottege.app.domain.events.HarvestEventStatus
import ru.mycottege.app.domain.events.HarvestEventsGenerator
import ru.mycottege.app.ui.common.AppScreen
import ru.mycottege.app.ui.crops.cropTitleRes
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayScreen() {
  val context = LocalContext.current
  val dao = remember { DbProvider.get(context).plantingDao() }
  val plantings by dao.observeAll().collectAsState(initial = emptyList())

  val today = LocalDate.now()
  val horizonDays = 30

  val snapshots = remember(plantings) { plantings.map { it.toSnapshot() } }
  val events = remember(snapshots, today) {
    HarvestEventsGenerator.generate(
      plantings = snapshots,
      today = today,
      horizonDays = horizonDays
    )
  }

  AppScreen(R.string.tab_today) {
    if (events.isEmpty()) {
      Text(text = stringResource(R.string.today_empty, horizonDays))
    } else {
      HarvestEventsList(events)
    }
  }
}

@Composable
private fun HarvestEventsList(events: List<HarvestEvent>) {
  val formatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

  LazyColumn(
    modifier = Modifier.fillMaxWidth()
  ) {
    items(events, key = { "${it.plantingId}_${it.status}_${it.sortDate}" }) { e ->
      val cropTitle = stringResource(cropTitleRes(e.cropId))

      val title = when (e.status) {
        HarvestEventStatus.ACTIVE ->
          stringResource(R.string.today_harvest_active_title, cropTitle)

        HarvestEventStatus.UPCOMING ->
          stringResource(R.string.today_harvest_upcoming_title, cropTitle)
      }

      ElevatedCard(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp)
        )

        val variety = e.varietyName?.takeIf { it.isNotBlank() }
        if (variety != null) {
          Text(
            text = stringResource(R.string.plantings_variety_title, variety),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 12.dp, top = 4.dp, end = 12.dp)
          )
        }

        when (e.status) {
          HarvestEventStatus.ACTIVE -> {
            Text(
              text = stringResource(
                R.string.today_harvest_until,
                e.windowEnd.format(formatter)
              ),
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 12.dp)
            )
          }

          HarvestEventStatus.UPCOMING -> {
            Text(
              text = stringResource(
                R.string.today_harvest_window,
                e.windowStart.format(formatter),
                e.windowEnd.format(formatter)
              ),
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 12.dp)
            )
          }
        }
      }
    }
  }
}
