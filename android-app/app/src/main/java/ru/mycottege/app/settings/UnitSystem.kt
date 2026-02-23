package ru.mycottege.app.settings

// Система единиц: метрическая или имперская (US).
enum class UnitSystem(val id: Int) {
  METRIC(0),
  IMPERIAL(1);

  companion object {
    fun fromId(id: Int): UnitSystem = when (id) {
      IMPERIAL.id -> IMPERIAL
      else -> METRIC
    }
  }
}
