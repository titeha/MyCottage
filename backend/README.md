# Backend (скелет)

Это заготовка облачной части: API + сервисы (auth, sync, медиа, погода, справочники, ИИ).

Документы:
- `docs/API_SPEC.md` — список эндпоинтов (черновик)
- `backend/openapi.yaml` — OpenAPI контракты (минимальный скелет, расширяется)

Рекомендуемый путь для старта:
1) Реализовать `/health`
2) Подключить OIDC (Keycloak) и защитить эндпоинты
3) CRUD: Sites/Zones/Plantings/Tasks/Journal
4) Plot Planner: PlotPlans/Features/Placements
5) Media upload: signed URLs (S3)
6) Weather cache: daily forecast + алерты заморозков (server side)
7) Sync: upload changes + delta + конфликты

См. `infra/docker-compose.yml` для локального dev окружения.
