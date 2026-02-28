package ru.mycottege.app.ui.cultivation

import androidx.annotation.StringRes
import ru.mycottege.app.R
import ru.mycottege.app.domain.cultivation.CultivationMethod

@StringRes
fun cultivationTitleRes(method: CultivationMethod): Int = when (method) {
  CultivationMethod.OPEN_GROUND -> R.string.cultivation_open_ground
  CultivationMethod.GREENHOUSE -> R.string.cultivation_greenhouse
}
