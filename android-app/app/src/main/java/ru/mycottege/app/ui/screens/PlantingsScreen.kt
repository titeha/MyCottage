package ru.mycottege.app.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.mycottege.app.R
import ru.mycottege.app.ui.common.AppScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import ru.mycottege.app.data.local.db.DbProvider
import ru.mycottege.app.data.local.db.PlantingEntity
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.rememberCoroutineScope
import ru.mycottege.app.util.localDateToUtcEpochMillis
import ru.mycottege.app.util.utcEpochMillisToLocalDate

@Composable
fun PlantingsScreen() {
  val context = LocalContext.current
  val dao = remember { DbProvider.get(context).plantingDao() }
  val scope = rememberCoroutineScope()

  val plantings by dao.observeAll().collectAsState(initial = emptyList())

  var showAddDialog by remember { mutableStateOf(false) }

  AppScreen(R.string.tab_plantings) {

    if (plantings.isEmpty()) {
      Text(text = stringResource(R.string.plantings_empty))
      Spacer(modifier = Modifier.height(8.dp))
      Spacer(modifier = Modifier.weight(1f, fill = true))
    } else {
      PlantingsList(
        items = plantings,
        modifier = Modifier.weight(1f, fill = true),
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
      onAdd = { cropName, plantedDate ->
        scope.launch {
          dao.insert(
            PlantingEntity(
              cropName = cropName.trim(),
              plantedDate = plantedDate
            )
          )
        }
      }
    )
  }
}

@Composable
private fun PlantingsList(
  items: List<PlantingEntity>,
  modifier: Modifier = Modifier
) {
  val formatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

  LazyColumn(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(items, key = { it.id }) { item ->
      ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(item.cropName, style = MaterialTheme.typography.titleMedium)
          Text(
            text = stringResource(
              R.string.plantings_planted_on,
              item.plantedDate.format(formatter)
            ),
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    }
  }
}

@Composable
private fun AddPlantingDialog(
  onDismiss: () -> Unit,
  onAdd: (String, LocalDate) -> Unit
) {
  var crop by remember { mutableStateOf("") }

  var selectedDate by remember { mutableStateOf(LocalDate.now()) }
  var showDatePicker by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = stringResource(R.string.plantings_add)) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
          value = crop,
          onValueChange = { crop = it },
          label = { Text(text = stringResource(R.string.plantings_crop_label)) },
          placeholder = { Text(text = stringResource(R.string.plantings_crop_placeholder)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

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

        // Можно показать выбранную дату текстом:
        Text(
          text = selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
          style = MaterialTheme.typography.bodySmall
        )
      }
    },
    confirmButton = {
      TextButton(
        enabled = crop.isNotBlank(),
        onClick = {
          onAdd(crop, selectedDate)
          onDismiss()
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
