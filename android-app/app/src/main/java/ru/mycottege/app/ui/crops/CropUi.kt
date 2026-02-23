package ru.mycottege.app.ui.crops

import ru.mycottege.app.R
import ru.mycottege.app.domain.crops.CropId

fun cropTitleRes(id: CropId): Int = when (id) {
  CropId.RADISH -> R.string.crop_radish
  CropId.CUCUMBER -> R.string.crop_cucumber
  CropId.TOMATO -> R.string.crop_tomato
}
