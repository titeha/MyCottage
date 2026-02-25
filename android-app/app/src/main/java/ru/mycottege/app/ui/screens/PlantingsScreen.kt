package ru.mycottege.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.mycottege.app.R
import ru.mycottege.app.data.local.db.DbProvider
import ru.mycottege.app.data.local.db.PlantingEntity
import ru.mycottege.app.domain.crops.CropCatalog
import ru.mycottege.app.domain.crops.CropId
import ru.mycottege.app.domain.harvest.HarvestWindowCalculator
import ru.mycottege.app.ui.common.AppScreen
import ru.mycottege.app.ui.crops.cropTitleRes
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PlantingsScreen() {
  val context = LocalContext.current
  val dao = remember { DbProvider.get(context).plantingDao() }
  val scope = rememberCoroutineScope()

  val plantings by dao.observeAll().collectAsState(initial = emptyList())

  var showAddDialog by remember { mutableStateOf(false) }
  var deleteCandidate by remember { mutableStateOf<PlantingEntity?>(null) }

  AppScreen(R.string.tab_plantings) {
    if (plantings.isEmpty()) {
      Text(text = stringResource(R.string.plantings_empty))
      Spacer(modifier = Modifier.height(8.dp))
      Spacer(modifier = Modifier.weight(1f, fill = true))
    } else {
      PlantingsList(
        items = plantings,
        modifier = Modifier.weight(1f, fill = true),
        onDeleteRequest = { deleteCandidate = it }
      )
    }

    Button(
      onClick = { showAddDialog = true },
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(text = stringResource(R.string.plantings_add))
    }
  }

  if (showAddDialog) {
    AddPlantingDialog(
      onDismiss = { showAddDialog = false },
      onAdd = { cropId, cropName, varietyName, plantedDate ->
        val varietyToSave = varietyName?.trim()?.takeIf { it.isNotBlank() }

        scope.launch {
          dao.insert(
            PlantingEntity(
              cropId = cropId,
              cropName = cropName.trim(),
              varietyName = varietyToSave,
              plantedDate = plantedDate
            )
          )
        }
      }
    )
  }

  val candidate = deleteCandidate
  if (candidate != null) {
    AlertDialog(
      onDismissRequest = { deleteCandidate = null },
      title = { Text(stringResource(R.string.plantings_delete_title)) },
      text = { Text(stringResource(R.string.plantings_delete_text, candidate.cropName)) },
      confirmButton = {
        TextButton(
          onClick = {
            scope.launch { dao.deleteById(candidate.id) }
            deleteCandidate = null
          }
        ) {
          Text(stringResource(R.string.common_delete))
        }
      },
      dismissButton = {
        TextButton(onClick = { deleteCandidate = null }) {
          Text(stringResource(R.string.common_cancel))
        }
      }
    )
  }
}

@Composable
private fun PlantingsList(
  items: List<PlantingEntity>,
  modifier: Modifier = Modifier,
  onDeleteRequest: (PlantingEntity) -> Unit
) {
  val formatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

  LazyColumn(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(items, key = { it.id }) { item ->
      val knownCropId = remember(item.cropId) {
        item.cropId?.let { runCatching { CropId.valueOf(it) }.getOrNull() }
      }

      val title = if (knownCropId != null) {
        stringResource(cropTitleRes(knownCropId))
      } else {
        item.cropName
      }

      val harvestWindow = remember(item.plantedDate, knownCropId) {
        knownCropId?.let { id ->
          HarvestWindowCalculator.calculate(item.plantedDate, CropCatalog.get(id))
        }
      }

      ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(title, style = MaterialTheme.typography.titleMedium)

          Text(
            text = stringResource(
              R.string.plantings_planted_on,
              item.plantedDate.format(formatter)
            ),
            style = MaterialTheme.typography.bodySmall
          )

          if (harvestWindow != null) {
            Text(
              text = stringResource(
                R.string.plantings_harvest_window,
                harvestWindow.start.format(formatter),
                harvestWindow.end.format(formatter)
              ),
              style = MaterialTheme.typography.bodySmall
            )
          }

          TextButton(onClick = { onDeleteRequest(item) }) {
            Text(text = stringResource(R.string.plantings_delete))
          }
        }
      }
    }
  }
}

@Composable
private fun AddPlantingDialog(
  onDismiss: () -> Unit,
  onAdd: (String?, String, String?, LocalDate) -> Unit
) {
  val knownCrops = remember { listOf(CropId.RADISH, CropId.CUCUMBER, CropId.TOMATO) }

  // null = пользовательская культура (ввод вручную)
  var selectedCrop by remember { mutableStateOf<CropId?>(CropId.RADISH) }
  var customName by remember { mutableStateOf("") }
  var variety by remember { mutableStateOf("") }

  var selectedDate by remember { mutableStateOf(LocalDate.now()) }
  var showDatePicker by remember { mutableStateOf(false) }

  val formatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

  val canAdd = selectedCrop != null || customName.isNotBlank()

  // Важно: stringResource — @Composable, поэтому вычисляем имя тут, а не внутри onClick.
  val cropNameToSave = if (selectedCrop != null) {
    stringResource(cropTitleRes(selectedCrop!!))
  } else {
    customName.trim()
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = stringResource(R.string.plantings_add)) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
          text = stringResource(R.string.plantings_crop_label),
          style = MaterialTheme.typography.titleMedium
        )

        knownCrops.forEach { crop ->
          CropRadioRow(
            title = stringResource(cropTitleRes(crop)),
            selected = selectedCrop == crop,
            onClick = { selectedCrop = crop }
          )
        }

        CropRadioRow(
          title = stringResource(R.string.crop_custom),
          selected = selectedCrop == null,
          onClick = { selectedCrop = null }
        )

        if (selectedCrop == null) {
          OutlinedTextField(
            value = variety,
            onValueChange = { variety = it },
            label = { Text(text = stringResource(R.string.plantings_variety_label)) },
            placeholder = { Text(text = stringResource(R.string.plantings_variety_placeholder)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = stringResource(R.string.plantings_date_label),
            modifier = Modifier.weight(1f)
          )
          TextButton(onClick = { showDatePicker = true }) {
            Text(text = stringResource(R.string.plantings_pick_date))
          }
        }

        Text(
          text = selectedDate.format(formatter),
          style = MaterialTheme.typography.bodySmall
        )
      }
    },
    confirmButton = {
      TextButton(
        enabled = canAdd,
        onClick = {
          val varietyToSave = variety.trim().takeIf { it.isNotBlank() }
          val cropIdToSave: String? = selectedCrop?.name
          onAdd(cropIdToSave, cropNameToSave, varietyToSave, selectedDate)
          onDismiss() // закрываем диалог
        }
      ) {
        Text(text = stringResource(R.string.common_add))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = stringResource(R.string.common_cancel))
      }
    }
  )

  if (showDatePicker) {
    PlantingDatePickerDialog(
      initialDate = selectedDate,
      onDismiss = { showDatePicker = false },
      onDateSelected = {
        selectedDate = it
        showDatePicker = false
      }
    )
  }
}

@Composable
private fun CropRadioRow(
  title: String,
  selected: Boolean,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    RadioButton(selected = selected, onClick = onClick)
    Text(text = title, modifier = Modifier.padding(start = 8.dp))
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlantingDatePickerDialog(
  initialDate: LocalDate,
  onDismiss: () -> Unit,
  onDateSelected: (LocalDate) -> Unit
) {
  val state = rememberDatePickerState(
    initialSelectedDateMillis = localDateToUtcEpochMillis(initialDate)
  )

  DatePickerDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(
        onClick = {
          val millis = state.selectedDateMillis
          if (millis != null) {
            onDateSelected(utcEpochMillisToLocalDate(millis))
          } else {
            onDismiss()
          }
        }
      ) {
        Text(stringResource(R.string.common_ok))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.common_cancel))
      }
    }
  ) {
    DatePicker(state = state)
  }
}

private val UTC: ZoneId = ZoneId.of("UTC")

// Чтобы не ловить сдвиг даты из‑за часовых поясов — работаем через UTC.
private fun localDateToUtcEpochMillis(date: LocalDate): Long =
  date.atStartOfDay(UTC).toInstant().toEpochMilli()

private fun utcEpochMillisToLocalDate(millis: Long): LocalDate =
  Instant.ofEpochMilli(millis).atZone(UTC).toLocalDate()
