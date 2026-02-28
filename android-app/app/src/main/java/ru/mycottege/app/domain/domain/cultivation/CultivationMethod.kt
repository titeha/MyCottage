package ru.mycottege.app.domain.cultivation

enum class CultivationMethod {
  OPEN_GROUND,
  GREENHOUSE;

  companion object {
    fun fromStorage(value: String?): CultivationMethod {
      return runCatching { value?.let { valueOf(it) } }.getOrNull() ?: OPEN_GROUND
    }
  }
}
