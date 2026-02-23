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

@Composable
fun DisclaimerDialog(onAccept: () -> Unit) {
  AlertDialog(
    onDismissRequest = { /* не даём закрыть без принятия */ },
    title = { Text("Важная информация") },
    text = {
      Column(
        modifier = Modifier
          .heightIn(max = 360.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          "«Моя дача» — информационный сервис. Рекомендации и расчёты сроков носят справочный характер.\n\n" +
            "Мы не даём гарантий урожая или сохранности растений.\n\n" +
            "Результат зависит от множества факторов, включая те, которые пользователь может не указать (например: уже внесённые удобрения пролонгированного действия, обработки, особенности почвы и полива).\n\n" +
            "Любые действия (подкормки, обработки, обрезка и т.д.) пользователь выполняет самостоятельно и на свой риск.\n\n" +
            "При использовании СЗР и агрохимикатов всегда следуйте официальной инструкции производителя и соблюдайте меры безопасности.\n\n" +
            "При сомнениях обращайтесь к специалистам."
        )
      }
    },
    confirmButton = {
      TextButton(onClick = onAccept) {
        Text("Понимаю и принимаю")
      }
    }
  )
}
