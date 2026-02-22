# API Spec (черновик, v0.5)

Цель: единые контракты для Android/iOS/Web и облачных сервисов.

## 1) Принципы
- JSON over HTTPS
- Версионирование: `/api/v1`
- Идентификаторы: UUID
- Аутентификация: OIDC (Bearer JWT)
- Offine‑first синк: клиент отправляет изменения пачками; сервер возвращает конфликт/мердж‑решение.

## 2) Core (ядро)

### Sites
- GET /api/v1/sites
- POST /api/v1/sites
- PATCH /api/v1/sites/{id}
- DELETE /api/v1/sites/{id}

### Zones
- GET /api/v1/sites/{siteId}/zones
- POST /api/v1/sites/{siteId}/zones
- PATCH /api/v1/zones/{id}
- DELETE /api/v1/zones/{id}

### Plantings
- GET /api/v1/sites/{siteId}/plantings
- POST /api/v1/sites/{siteId}/plantings
- PATCH /api/v1/plantings/{id}
- DELETE /api/v1/plantings/{id}

### Tasks
- GET /api/v1/sites/{siteId}/tasks?from=YYYY-MM-DD&to=YYYY-MM-DD
- POST /api/v1/sites/{siteId}/tasks
- PATCH /api/v1/tasks/{id}
- DELETE /api/v1/tasks/{id}

### Journal
- GET /api/v1/sites/{siteId}/journal?from=...&to=...
- POST /api/v1/sites/{siteId}/journal
- PATCH /api/v1/journal/{id}
- DELETE /api/v1/journal/{id}

### Media (фото)
- POST /api/v1/sites/{siteId}/media/upload (multipart)
- GET  /api/v1/media/{id}
- DELETE /api/v1/media/{id}

## 3) Plot Planner
- GET  /api/v1/sites/{siteId}/plot-plans
- POST /api/v1/sites/{siteId}/plot-plans
- GET  /api/v1/plot-plans/{planId}
- PATCH /api/v1/plot-plans/{planId}
- POST /api/v1/plot-plans/{planId}/features
- PATCH /api/v1/plot-features/{id}
- DELETE /api/v1/plot-features/{id}
- POST /api/v1/plot-plans/{planId}/placements
- PATCH /api/v1/plant-placements/{id}
- DELETE /api/v1/plant-placements/{id}

## 4) Weather / Agroclimate (ядро)
- GET /api/v1/sites/{siteId}/weather/daily?from=...&to=...
- POST /api/v1/sites/{siteId}/weather/sync (server fetch & cache)
- GET /api/v1/sites/{siteId}/agroclimate/profile

## 5) Soils (расширение)
- GET/POST/PATCH SoilProfile (site/zone scope)
- POST /api/v1/sites/{siteId}/soil/samples
- GET  /api/v1/sites/{siteId}/soil/samples
- POST /api/v1/sites/{siteId}/soil/plan/generate
- GET  /api/v1/sites/{siteId}/soil/plan

## 6) References (культуры/сорта/техкарты/химия/рецепты)
- GET /api/v1/ref/crops
- GET /api/v1/ref/varieties?cropId=...
- GET /api/v1/ref/cultivation-cards?cropId=...&varietyId=...
- GET /api/v1/ref/chemicals?query=...&category=...
- GET /api/v1/ref/recipes?cropId=...&tag=...

## 7) AI (чат, наблюдения, предложения задач)
### Chat
- POST /api/v1/ai/chat (text + context + optional media_ids)
- GET  /api/v1/ai/chat/{threadId}

### Observations / Issues
- GET  /api/v1/sites/{siteId}/observations
- POST /api/v1/sites/{siteId}/observations (manual)
- GET  /api/v1/sites/{siteId}/issues
- PATCH /api/v1/issues/{id}

### Task Proposals
- GET  /api/v1/sites/{siteId}/task-proposals
- POST /api/v1/task-proposals/{id}/accept (создать реальные задачи)
- POST /api/v1/task-proposals/{id}/reject

## 8) Automation (позже)
- POST /api/v1/sites/{siteId}/automation/connections
- GET  /api/v1/sites/{siteId}/automation/connections
- POST /api/v1/sites/{siteId}/automation/rules
- GET  /api/v1/sites/{siteId}/automation/rules
- PATCH /api/v1/automation/rules/{id}

