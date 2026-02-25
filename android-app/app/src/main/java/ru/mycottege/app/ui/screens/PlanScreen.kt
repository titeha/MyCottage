package ru.mycottege.app.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.mycottege.app.R
import ru.mycottege.app.data.local.db.DbProvider
import ru.mycottege.app.data.local.db.PlantingEntity
import ru.mycottege.app.domain.crops.CropCatalog
import ru.mycottege.app.domain.crops.CropId
import ru.mycottege.app.domain.harvest.HarvestWindowCalculator
import ru.mycottege.app.domain.timeline.TimelineRange
import ru.mycottege.app.domain.timeline.TimelineSegment
import ru.mycottege.app.domain.timeline.offsetInRange
import ru.mycottege.app.domain.timeline.segmentFor
import ru.mycottege.app.ui.common.AppScreen
import ru.mycottege.app.ui.crops.cropTitleRes
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.max
import kotlin.math.min

private val LABEL_WIDTH = 170.dp
private val HEADER_HEIGHT = 34.dp
private val ROW_HEIGHT = 56.dp
private val DAY_WIDTH = 6.dp   // масштаб: 1 день = 6dp (потом можно сделать зум)

private fun dx(days: Int) = DAY_WIDTH * days.toFloat()

@Composable
fun PlanScreen() {
  val context = LocalContext.current
  val dao = remember { DbProvider.get(context).plantingDao() }
  val plantings by dao.observeAll().collectAsState(initial = emptyList())

  val today = LocalDate.now()
  val range = remember(plantings, today) { computeRange(plantings, today) }
  val scrollState = rememberScrollState()

  AppScreen(R.string.tab_plan) {
    if (plantings.isEmpty()) {
      Text(text = stringResource(R.string.plan_empty))
    } else {
      PlanHeader(range = range, scrollState = scrollState, today = today)

      LazyColumn(
        modifier = Modifier.weight(1f, fill = true),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(plantings, key = { it.id }) { p ->
          PlanRow(
            planting = p,
            range = range,
            scrollState = scrollState,
            today = today
          )
        }
      }
    }
  }
}

private fun computeRange(plantings: List<PlantingEntity>, today: LocalDate): TimelineRange {
  // Делаем диапазон “разумным”: не гигантским, но чтобы было видно историю и будущее.
  val defaultPastDays = 30L
  val defaultFutureDays = 180L

  val minPlanted = plantings.minOfOrNull { it.plantedDate } ?: today
  var start = minOf(minPlanted, today.minusDays(defaultPastDays))

  // ограничим “историю” максимум 365 дней, чтобы диаграмма не стала километровой
  if (ChronoUnit.DAYS.between(start, today) > 365) {
    start = today.minusDays(365)
  }

  // попробуем расширить конец по расчетным окнам сбора (если культуры известны)
  val maxHarvestEnd = plantings.mapNotNull { e ->
    val cid = e.cropId?.let { runCatching { CropId.valueOf(it) }.getOrNull() } ?: return@mapNotNull null
    HarvestWindowCalculator.calculate(e.plantedDate, CropCatalog.get(cid)).end
  }.maxOrNull()

  var end = maxOf(maxHarvestEnd ?: today.plusDays(defaultFutureDays), today.plusDays(defaultFutureDays))

  // ограничим “будущее” максимум 365 дней
  if (ChronoUnit.DAYS.between(today, end) > 365) {
    end = today.plusDays(365)
  }

  if (end.isBefore(start)) end = start
  return TimelineRange(start = start, end = end)
}

@Composable
private fun PlanHeader(range: TimelineRange, scrollState: ScrollState, today: LocalDate) {
  val totalWidth = dx(range.daysCount)
  val tickFormatter = remember { DateTimeFormatter.ofPattern("dd.MM") }

  val ticks = remember(range) {
    val list = mutableListOf<LocalDate>()
    var d = range.start
    while (!d.isAfter(range.end)) {
      list.add(d)
      d = d.plusDays(30) // на старте достаточно “шкалы” раз в 30 дней
    }
    list
  }

  Row(modifier = Modifier.fillMaxWidth()) {
    Spacer(modifier = Modifier.width(LABEL_WIDTH))

    Box(
      modifier = Modifier
        .horizontalScroll(scrollState)
        .width(totalWidth)
        .height(HEADER_HEIGHT)
    ) {
      ticks.forEach { d ->
        val offsetDays = ChronoUnit.DAYS.between(range.start, d).toInt()
        Text(
          text = d.format(tickFormatter),
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.offset(x = dx(offsetDays))
        )
      }

      val todayOffset = offsetInRange(range, today)
      if (todayOffset != null) {
        Box(
          modifier = Modifier
            .offset(x = dx(todayOffset))
            .width(2.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
        )
      }
    }
  }
}

@Composable
private fun PlanRow(
  planting: PlantingEntity,
  range: TimelineRange,
  scrollState: ScrollState,
  today: LocalDate
) {
  val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

  val knownCropId = remember(planting.cropId) {
    planting.cropId?.let { runCatching { CropId.valueOf(it) }.getOrNull() }
  }

  val title = if (knownCropId != null) {
    stringResource(cropTitleRes(knownCropId))
  } else {
    planting.cropName
  }

  val harvestWindow = remember(planting.plantedDate, knownCropId) {
    knownCropId?.let { id ->
      HarvestWindowCalculator.calculate(planting.plantedDate, CropCatalog.get(id))
    }
  }

  val growthSeg: TimelineSegment? = remember(planting.plantedDate, harvestWindow, range) {
    harvestWindow?.let {
      // Рост до начала окна сбора (день перед start)
      val growthEnd = it.start.minusDays(1)
      segmentFor(range, planting.plantedDate, growthEnd)
    }
  }

  val harvestSeg: TimelineSegment? = remember(harvestWindow, range) {
    harvestWindow?.let { segmentFor(range, it.start, it.end) }
  }

  val todayOffset = remember(range, today) { offsetInRange(range, today) }

  val totalWidth = dx(range.daysCount)

  ElevatedCard(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Левая колонка (фиксированная)
      Column(modifier = Modifier.width(LABEL_WIDTH)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
          text = stringResource(R.string.plantings_planted_on, planting.plantedDate.format(dateFormatter)),
          style = MaterialTheme.typography.bodySmall
        )
      }

      // Правая часть (таймлайн, горизонтальный скролл общий для всех строк)
      Box(
        modifier = Modifier
          .horizontalScroll(scrollState)
          .width(totalWidth)
          .height(ROW_HEIGHT)
      ) {
        // линия "сегодня"
        if (todayOffset != null) {
          Box(
            modifier = Modifier
              .offset(x = dx(todayOffset))
              .width(2.dp)
              .fillMaxHeight()
              .background(MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
          )
        }

        // сегмент роста (если есть)
        if (growthSeg != null) {
          SegmentBar(
            segment = growthSeg,
            top = 18.dp,
            height = 6.dp,
            color = MaterialTheme.colorScheme.secondary
          )
        }

        // сегмент окна сбора (если есть)
        if (harvestSeg != null) {
          SegmentBar(
            segment = harvestSeg,
            top = 28.dp,
            height = 10.dp,
            color = MaterialTheme.colorScheme.primary
          )
        }

        // если культура неизвестна — покажем маркер посадки
        if (knownCropId == null) {
          val plantedOffset = offsetInRange(range, planting.plantedDate)
          if (plantedOffset != null) {
            Box(
              modifier = Modifier
                .offset(x = dx(plantedOffset), y = 26.dp)
                .size(8.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SegmentBar(
  segment: TimelineSegment,
  top: Dp,
  height: Dp,
  color: androidx.compose.ui.graphics.Color
) {
  Box(
    modifier = Modifier
      .offset(x = dx(segment.offsetDays), y = top)
      .width(dx(max(1, segment.lengthDays)))
      .height(height)
      .background(color, RoundedCornerShape(50))
  )
}
