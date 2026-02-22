# Структура проекта (предложение)

## Репозиторий
- `android-app/` — Android клиент (офлайн‑first).
- `ios-app/` — iOS заготовка (позже).
- `shared/` — общие контракты/домен (опционально KMP/OpenAPI‑генерация).
- `web-portal/` — веб‑портал (сообщество + инструменты).
- `backend/` — Cloud API и сервисы.
- `infra/` — инфраструктура (dev/stage/prod).
- `docs/` — ТЗ, архитектура, модель данных, API, roadmap.
- `scripts/` — генераторы справочников/миграции/утилиты.
- `data/` — справочники/шаблоны (без персональных данных).

## Облако (в прод)
- API (контейнеры)
- PostgreSQL (managed или self-host)
- Object Storage (S3)
- Auth (OIDC)
- Weather provider (API/кэш)
- AI inference (LLM/CV) — managed или self-host (по мере роста)
- Forum engine (опционально; отдельный сервис)
- Monitoring/Logs/Tracing — позже

## Среды
- dev: `docker compose`
- stage: минимум близкий к prod (тот же набор сервисов)
- prod: managed сервисы по возможности (PostgreSQL, Object Storage, Auth)

- docs/TIMELINE_DIAGRAM.md — диаграмма сезона (Timeline/Gantt)
