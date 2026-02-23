package ru.mycottege.app.legal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import ru.mycottege.app.R

@Composable
fun DisclaimerDialog(onAccept: () -> Unit) {
  AlertDialog(
    onDismissRequest = { /* не даём закрыть без принятия */ },
    title = { stringResource(R.string.disclaimer_title) },
    text = {
      Column(
        modifier = Modifier
          .heightIn(max = 360.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Text(stringResource(R.string.disclaimer_text))
      }
    },
    confirmButton = {
      TextButton(onClick = onAccept) {
        Text(stringResource(R.string.disclaimer_accept))
      }
    }
  )
}
