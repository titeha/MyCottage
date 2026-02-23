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
import androidx.compose.runtime.mutableStateListOf
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

private data class PlantingUi(
  val id: Long,
  val cropName: String,
  val plantedDate: LocalDate,
)

@Composable
fun PlantingsScreen() {
  val plantings = remember { mutableStateListOf<PlantingUi>() }
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
      onAdd = { cropName ->
        plantings.add(
          PlantingUi(
            id = System.currentTimeMillis(),
            cropName = cropName.trim(),
            plantedDate = LocalDate.now()
          )
        )
        showAddDialog = false
      }
    )
  }
}

@Composable
private fun PlantingsList(
  items: List<PlantingUi>,
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
  onAdd: (String) -> Unit
) {
  var crop by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = stringResource(R.string.plantings_add)) },
    text = {
      OutlinedTextField(
        value = crop,
        onValueChange = { crop = it },
        label = { Text(text = stringResource(R.string.plantings_crop_label)) },
        placeholder = { Text(text = stringResource(R.string.plantings_crop_placeholder)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
      )
    },
    confirmButton = {
      TextButton(
        enabled = crop.isNotBlank(),
        onClick = { onAdd(crop) }
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
}
