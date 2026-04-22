# Личный отчёт и карта проекта

## Главная идея

Проект больше не привязан к профилям с номерами лабораторных.
Есть два понятных режима:

- `postgres` - основной рабочий режим: PostgreSQL, JPA, Flyway;
- `memory` - быстрый режим без БД: данные в памяти.

Так проект проще расширять: новые лабораторные добавляют функциональность, а не новые случайные профили.

## PostgreSQL режим

Используется по умолчанию.

Команды:

```bash
docker compose up -d postgres
./gradlew bootRun
```

Что показать:

- `application.properties` - профиль по умолчанию `postgres`;
- `application-postgres.properties` - подключение к БД, Flyway, Hibernate `validate`;
- `repository/jpa` - JPA-реализация репозиториев;
- `db/migration` - создание схемы и стартовые данные.

Проверка:

```bash
curl http://localhost:8080/flights
curl "http://localhost:8080/flights/free-seats?destination=Moscow&date=2026-06-10"
```

## Memory режим

Команда:

```bash
./gradlew bootRun --args='--spring.profiles.active=memory'
```

Что показать:

- `application-memory.properties` - отключение JDBC/JPA/Flyway;
- `MemoryAutoConfiguration` - дополнительное отключение автоконфигурации БД;
- `MemoryDataInitializer` - стартовые тестовые данные;
- `repository/memory` - хранение в `HashMap`.

## Docker стенд

Команда:

```bash
docker compose up -d --build
```

Что показать:

- `Dockerfile` - сборка jar и запуск приложения;
- `docker-compose.yml` - сервисы `app` и `postgres`;
- `depends_on` + `healthcheck` - приложение ждёт готовность PostgreSQL.

Проверка:

```bash
docker compose ps
curl http://localhost:8080/flights
docker compose exec -T postgres psql -U postgres -d airline_tickets -c "select installed_rank, version, description, success from flyway_schema_history order by installed_rank;"
```

## Нагрузка k6

Файлы:

- `k6/load-profile.js` - `ramping-vus`;
- `k6/lab4-load-test.js` - создание рейса и запрос статистики свободных мест;
- `scripts/plot_k6_results.py` - график времени ответа;
- `scripts/seed_data.py` - заполнение данных перед нагрузкой.

Команды:

```bash
k6 run --out json=reports/k6-lab4-result.json k6/lab4-load-test.js
python3 scripts/plot_k6_results.py --input reports/k6-lab4-result.json --output reports/k6-lab4-response-time.png
```

## Карта кода

Точка входа:

- `Application` - запускает Spring Boot.

REST API:

- `FlightController` - `/flights`, свободные места, доступность рейсов;
- `PassengerController` - `/passengers`;
- `BookingController` - `/bookings`;
- `MaintenanceController` - `/clear` для очистки перед нагрузкой.

DTO:

- `controller/dto/*Request` - входной JSON;
- `controller/dto/*Response` - выходной JSON;
- `FlightAvailabilityResponse` - статистика свободных мест.

Бизнес-логика:

- `FlightService` - рейсы, валидация, расчёт свободных мест;
- `PassengerService` - пассажиры и валидация;
- `BookingService` - проверка рейса, пассажира, места и вместимости.

Хранение:

- `repository/*Repository` - общий контракт;
- `repository/jpa` - PostgreSQL;
- `repository/memory` - память.

БД:

- `V1__create_airline_schema.sql` - таблицы и ограничения;
- `V2__insert_test_data.sql` - стартовые данные;
- `flyway_schema_history` - история применённых миграций.

## Что говорить на защите

1. Номера лабораторных не зашиты в профили, потому что это плохо масштабируется.
2. Основной режим приложения - `postgres`.
3. Схема БД управляется Flyway, Hibernate только проверяет её через `validate`.
4. Memory-режим оставлен как простой способ показать бизнес-логику без инфраструктуры.
5. Нагрузочный тест k6 создаёт простые сущности `Flight` и проверяет статистический endpoint свободных мест.
