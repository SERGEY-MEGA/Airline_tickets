# Личный отчёт по LAB1-LAB5

## LAB1

Данные хранятся в памяти приложения через `repository/memory`.
Профиль по умолчанию: `lab1`.
База данных не нужна.

Показать:

- `application.properties`
- `Lab1DataInitializer`
- `repository/memory`
- `GET /flights`
- `POST /bookings`

## LAB2

Данные хранятся в PostgreSQL через Spring Data JPA.
Профиль: `lab2`.
Схему создаёт Hibernate: `spring.jpa.hibernate.ddl-auto=create`.
Тестовые данные загружаются через `data.sql`.
Flyway отключён.

Показать:

- `application-lab2.properties`
- JPA entities в `model`
- `repository/jpa`
- `data.sql`

Запуск:

```bash
docker compose up -d postgres
./gradlew bootRun --args='--spring.profiles.active=lab2'
```

Проверка:

```bash
curl http://localhost:8080/flights
curl "http://localhost:8080/flights/free-seats?destination=Moscow&date=2026-06-10"
```

## LAB3

Приложение и PostgreSQL запускаются через Docker Compose.
Профиль: `lab3`.
Схему и стартовые данные создаёт Flyway.
Hibernate не создаёт таблицы, а проверяет схему: `ddl-auto=validate`.
`data.sql` отключён.

Показать:

- `Dockerfile`
- `docker-compose.yml`
- `application-lab3.properties`
- `db/migration/V1__create_airline_schema.sql`
- `db/migration/V2__insert_test_data.sql`
- `flyway_schema_history`

Запуск:

```bash
docker compose up -d --build
```

Проверка:

```bash
docker compose ps
curl http://localhost:8080/flights
curl "http://localhost:8080/flights/free-seats?destination=Moscow&date=2026-06-10"
docker compose exec -T postgres psql -U postgres -d airline_tickets -c "select installed_rank, version, description, success from flyway_schema_history order by installed_rank;"
```

## LAB4

Нагрузочное тестирование через k6.
Используется стандартный пакет `k6/http` и executor `ramping-vus`.

Что нагружается:

- `POST /flights` - создание простой сущности `Flight`, у неё нет ссылок на другие таблицы.
- `GET /flights/free-seats?destination=Moscow&date=2026-06-10` - дополнительный endpoint со статистикой свободных мест.

Файлы:

- `k6/load-profile.js` - общий профиль нагрузки.
- `k6/lab4-load-test.js` - сценарий LAB4.
- `scripts/plot_k6_results.py` - строит график зависимости времени отклика.

Команды:

```bash
docker compose up -d --build
k6 run --out json=reports/k6-lab4-result.json k6/lab4-load-test.js
python3 scripts/plot_k6_results.py --input reports/k6-lab4-result.json --output reports/k6-lab4-response-time.png
```

Что сказать:

`ramping-vus` постепенно увеличивает число виртуальных пользователей.
В JSON-результате k6 есть точки `http_req_duration`.
Python-скрипт читает эти точки и строит PNG-график времени ответа.

## LAB5

Перед нагрузочным тестом данные создаются внешним Python-скриптом.
Это удобнее для k6, потому что можно быстро менять объём данных через `--count`.

Новые файлы:

- `scripts/seed_data.py` - очищает сервис через `/clear` и создаёт данные.
- `k6/load-profile.js` - профиль нагрузки k6.
- `k6/booking-load-test.js` - простой k6-сценарий чтения API.
- `scripts/plot_k6_results.py` - строит график по JSON-результату k6.

Команды:

```bash
pip install -r requirements-lab5.txt
python3 scripts/seed_data.py --count 500 --endpoint bookings
k6 run --out json=reports/k6-result.json k6/booking-load-test.js
python3 scripts/plot_k6_results.py --input reports/k6-result.json
```

## Ключевые отличия

- LAB1: память, без БД, самый простой запуск.
- LAB2: PostgreSQL + JPA, схема создаётся Hibernate.
- LAB3: Docker + PostgreSQL + Flyway, схема управляется миграциями.
- LAB4: k6 нагружает REST API и строится график времени ответа.
- LAB5: данные для нагрузки создаются отдельным скриптом, нагрузка запускается через k6.

## Что отвечать на доп. задачки

Если просят поменять объём данных: изменить `--count`.
Если просят тестировать другой endpoint: изменить `--endpoint` или k6-сценарий.
Если просят очистить данные: `DELETE /clear`.
Если просят показать график: запустить k6 с `--out json=...`, потом `plot_k6_results.py`.

## Карта кода

Точка входа:

- `Application` - запускает Spring Boot.

REST API:

- `FlightController` - endpoints `/flights`, включая свободные места.
- `PassengerController` - endpoints `/passengers`.
- `BookingController` - endpoints `/bookings`.
- `MaintenanceController` - учебный endpoint `/clear` для LAB5.

DTO:

- `controller/dto/*Request` - JSON, который приходит в `POST`/`PUT`.
- `controller/dto/*Response` - JSON, который отдаётся наружу.
- `FlightAvailabilityResponse` - ответ для расчёта свободных мест.

Бизнес-логика:

- `FlightService` - валидация рейсов и расчёт свободных мест.
- `PassengerService` - валидация пассажиров.
- `BookingService` - проверяет рейс, пассажира, занятость места и вместимость.

Хранение:

- `repository/*Repository` - общий интерфейс, чтобы сервис не зависел от способа хранения.
- `repository/memory` - LAB1, данные в `HashMap`.
- `repository/jpa` - LAB2/LAB3, данные в PostgreSQL через Spring Data JPA.

БД и профили:

- `application.properties` - профиль по умолчанию `lab1`.
- `application-lab2.properties` - PostgreSQL + JPA + `data.sql`.
- `application-lab3.properties` - PostgreSQL + Flyway + Hibernate `validate`.
- `data.sql` - тестовые данные только для LAB2.
- `db/migration/V1__create_airline_schema.sql` - DDL для LAB3.
- `db/migration/V2__insert_test_data.sql` - тестовые данные для LAB3.

Docker:

- `Dockerfile` - сборка jar и запуск приложения в контейнере.
- `docker-compose.yml` - контейнеры `app` и `postgres`.

Нагрузка:

- `scripts/seed_data.py` - заполняет сервис данными перед k6.
- `k6/load-profile.js` - профиль нагрузки.
- `k6/lab4-load-test.js` - LAB4-сценарий: создание рейса и статистика свободных мест.
- `k6/booking-load-test.js` - k6-сценарий.
- `scripts/plot_k6_results.py` - график по результатам k6.
