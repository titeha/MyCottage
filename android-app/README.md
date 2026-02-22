# Android App (скелет)

Цель R1: офлайн‑дневник дачника + климат ядра + план участка (Plot Planner v1), с поддержкой планшетов.

Рекомендации по стеку:
- Kotlin + Jetpack Compose (Material 3)
- Room (SQLite)
- WorkManager (уведомления, синк, обновление погоды)
- DataStore (настройки)
- CameraX (фото‑наблюдения, позже)

Архитектура:
- Clean Architecture (UI → Domain → Data)
- Offline-first: локальная БД — источник истины
- Sync engine: очередь изменений + конфликты
- Adaptive UI: phone/tablet (multi‑pane)

Документы:
- `docs/TECHNICAL_SPEC.md`
- `docs/UI_UX.md`
- `docs/PLOT_PLANNER.md`
- `docs/WEATHER_AGROCLIMATE.md`

