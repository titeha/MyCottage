# Модель данных (черновик, v0.5)

Ниже — минимальная ER‑модель для ядра и расширений.  
Принцип: **офлайн‑first** — те же сущности должны жить локально (Room/SQLite) и синхронизироваться в облако.

## 1) Ядро (MVP)

### User
- id (UUID)
- email / phone (в зависимости от auth)
- created_at

### Site (Участок)
- id (UUID)
- user_id
- name
- region_text
- latitude, longitude (nullable)
- hardiness_zone (nullable, например "7a")
- created_at, updated_at, deleted_at

### Zone (Зона участка)
- id
- site_id
- name
- type (BED/GREENHOUSE/GARDEN_ROW/FLOWERBED/CONTAINER/OTHER)
- sun_exposure (SUN/PART_SHADE/SHADE, nullable)
- soil_profile_override_id (nullable)
- note (text, nullable)
- created_at, updated_at, deleted_at

### Planting (Посадка/Растение)
- id
- site_id
- zone_id
- crop_id (ссылка на справочник культур)
- variety_id (nullable) / variety_text (nullable)
- planting_method (SEEDS/SEEDLING/SAPLING/OTHER)
- planted_at (date)
- quantity (nullable)
- status (ACTIVE/HARVESTED/REMOVED)
- note (text, nullable)
- created_at, updated_at, deleted_at

### Task (Задача/Событие)
- id
- site_id
- zone_id (nullable)
- planting_id (nullable)
- type (CHECK_SPROUTS/HARVEST_WINDOW/WATER/FERTILIZE/PRUNE/INSPECT/…/CUSTOM)
- title
- start_at (date or datetime) — начало (точка или окно)
- end_at (date or datetime, nullable) — конец окна (если это диапазон)
- status (PLANNED/DONE/SKIPPED)
- created_by (SYSTEM/USER/AI_PROPOSAL)
- proposal_id (nullable) — если задача создана из предложения
- reminder_config (json, nullable)
- created_at, updated_at, deleted_at

### JournalEntry (Запись дневника) (Запись дневника)
- id
- site_id
- zone_id (nullable)
- planting_id (nullable)
- entry_at (datetime)
- text
- tags (json array)
- created_at, updated_at, deleted_at

### Photo (Медиа)
- id
- site_id
- owner_type (ZONE/PLANTING/JOURNAL/OBSERVATION/OTHER)
- owner_id
- local_uri (client)
- remote_url (server, nullable)
- sha256
- created_at, deleted_at

### CropReference (Справочник культур)
- crop_id
- name_ru
- category (VEG/GREEN/HERB/BERRY/FRUIT_TREE/SHRUB/ORNAMENTAL)
- default_days_to_harvest_min/max
- default_days_to_check_sprouts
- notes (text)

## 2) План участка (Plot Planner)

### PlotPlan
- id
- site_id
- name
- coordinate_system (LOCAL|METERS)
- north_angle_deg (nullable)
- scale_m_per_unit (nullable)
- created_at, updated_at, deleted_at

### PlotFeature
- id
- plan_id
- feature_type (ZONE|OBSTACLE|PATH|BUILDING|WATER|TREE|OTHER)
- geometry_geojson (text/json)
- properties_json (json)
- zone_id (nullable) — связь с Zone
- created_at, updated_at, deleted_at

### PlantPlacement
- id
- plan_id
- planting_id
- geometry_geojson (point/line/polygon)
- quantity (nullable)
- spacing_cm (nullable)
- created_at, updated_at, deleted_at

## 3) Климат/погода/агроклимат (ядро)

### WeatherDaily (кэш)
- site_id
- date
- tmin, tmax, tavg
- precipitation_mm
- et0_mm (nullable)
- sunshine_minutes (nullable) / shortwave_radiation_sum (nullable)
- source
- created_at

### AgroClimateProfile
- site_id
- computed_at
- last_spring_frost_p50 (date)
- first_autumn_frost_p50 (date)
- frost_free_days_p50 (int)
- gdd5_norm (float)
- gdd10_norm (float)
- precipitation_season_norm (float)
- sunshine_season_norm (float)
- confidence (LOW/MED/HIGH)

## 4) Почвы и плодородие (расширение)

### SoilProfile
- id
- scope (SITE/ZONE)
- scope_id
- texture_type (SAND/SANDY_LOAM/LOAM/CLAY/PEAT/CHERNOZEM/OTHER)
- drainage (DRY/NORMAL/WET)
- ph_value (nullable)
- source (MANUAL/MAP/LAB)
- updated_at

### SoilSample (лаборатория)
- id
- site_id
- zone_id (nullable)
- date_taken
- depth_cm_from, depth_cm_to
- lab_name (nullable)
- method_profile (nullable)
- results_json (json: pH, OM, N, P, K, микроэлементы… вместе с единицами)
- created_at

### SoilPlan (план восстановления)
- id
- site_id
- zone_id (nullable)
- created_at
- plan_version
- summary
- steps_json (json)
- source (MANUAL/ASSISTED/AI)

## 5) Справочник сортов (расширение)

### Variety
- id
- crop_id
- name
- type (SORT/HYBRID)
- maturity_days_min/max (nullable)
- recommended_conditions (OPEN_GROUND/GREENHOUSE/BOTH, nullable)
- region_admission (json array, nullable)
- notes_json (json)
- source (OFFICIAL/COMMUNITY/VENDOR/MANUAL)
- created_at, updated_at

## 6) Техкарты выращивания (расширение)

### CultivationCard
- id
- crop_id
- variety_id (nullable)
- applicability (json: зоны/условия/климат)
- operations_json (json: операции, триггеры, частоты, окна)
- created_at, updated_at

## 7) ИИ‑наблюдения и правила (расширение)

### Observation
- id
- site_id
- zone_id (nullable)
- planting_id (nullable)
- type (PEST/DISEASE/DEFICIENCY/PLANT_ID/OTHER)
- entity_id (nullable) — pest_id/disease_id/…
- entity_name (text)
- confidence (0..1)
- created_at
- source (AI_CHAT/CV/MANUAL)
- notes (text, nullable)

### Issue
- id
- site_id
- status (OPEN/RESOLVED)
- severity (LOW/MED/HIGH)
- title
- description
- created_at, updated_at

### TaskProposal
- id
- site_id
- issue_id (nullable)
- title
- type (INSPECT/ACTION/SPRAY/PRUNE/OTHER)
- due_window_start, due_window_end (nullable)
- requires_confirmation (bool)
- payload_json (json: ссылки на материалы/препараты/условия)
- created_at

## 8) Справочник СЗР/удобрений (расширение)

### ChemicalItem
- id
- category (INSECTICIDE/ACARICIDE/FUNGICIDE/HERBICIDE/FERTILIZER/AMENDMENT/STIMULANT/OTHER)
- trade_name
- active_ingredients_json (json array)
- formulation (nullable)
- registration_number (nullable)
- registrant (nullable)
- allowed_context (HOME_GARDEN/AGRI/BOTH/UNKNOWN)
- hazard_class (nullable)
- bee_hazard_class (nullable)
- restrictions_json (json)
- official_label_url (nullable)
- source_ref (text: версия/дата каталога)
- created_at, updated_at

## 9) Рецепты/заготовки (расширение)

### Recipe
- id
- name
- crop_ids_json (json array)
- tags_json (json array)
- ingredients_json (json)
- steps_text
- safety_notes (text, nullable)
- created_at, updated_at

## 10) Автоматизация (позже)

### AutomationConnection
- id
- site_id
- type (HOME_ASSISTANT/MQTT/OTHER)
- endpoint
- secret_ref (ссылка на секрет в vault/keystore)
- created_at, updated_at

### AutomationRule
- id
- site_id
- trigger_type (TASK_DUE/WEATHER/CRON)
- trigger_payload (json)
- action_payload (json)
- enabled (bool)
- created_at, updated_at

