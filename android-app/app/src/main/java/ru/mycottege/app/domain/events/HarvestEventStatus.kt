package ru.mycottege.app.domain.events

enum class HarvestEventStatus {
  ACTIVE,   // сейчас идёт сбор
  UPCOMING  // начнётся в ближайшие N дней
}
