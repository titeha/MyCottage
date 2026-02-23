package ru.mycottege.app.domain.crops

object CropCatalog {
  // Заглушка на старт. Потом уйдёт в JSON/БД/техкарты.
  val crops: List<CropSpec> = listOf(
    CropSpec(CropId.RADISH, 20, 30),
    CropSpec(CropId.CUCUMBER, 40, 55),
    CropSpec(CropId.TOMATO, 90, 120),
  )

  fun get(id: CropId): CropSpec = crops.first { it.id == id }
}
