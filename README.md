# Авиакасса

Учебный Spring Boot проект: REST-сервис для бронирования авиабилетов.

## Что реализовано

- рейсы: номер, направление, дата вылета, вместимость;
- пассажиры: ФИО, паспортные данные, контакты;
- бронирования: рейс, пассажир, класс обслуживания, место;
- расчёт свободных мест по направлению и дате;
- PostgreSQL через Spring Data JPA;
- миграции схемы и стартовых данных через Flyway;
- Docker Compose стенд;
- k6-сценарии для нагрузочного тестирования;
- Python-скрипт для заполнения данных перед нагрузкой.

## Режимы запуска

В проекте больше нет профилей с номерами лабораторных.
Профили называются по смыслу:

- `postgres` - основной режим: PostgreSQL + Flyway;
- `memory` - вспомогательный режим без БД, для быстрой локальной проверки.

По умолчанию активен `postgres`.

## Быстрый запуск через Docker

```bash
docker compose up -d --build
```

Проверка:

```bash
docker compose ps
curl http://localhost:8080/flights
curl "http://localhost:8080/flights/free-seats?destination=Moscow&date=2026-06-10"
open http://localhost:8080/swagger-ui.html
```

Проверка Flyway:

```bash
docker compose exec -T postgres psql -U postgres -d airline_tickets -c "select installed_rank, version, description, success from flyway_schema_history order by installed_rank;"
```

Остановка:

```bash
docker compose down
```

Чистый перезапуск с удалением данных:

```bash
docker compose down -v
docker compose up -d --build
```

## Локальный запуск с PostgreSQL

Сначала поднять PostgreSQL:

```bash
docker compose up -d postgres
```

Потом запустить приложение:

```bash
./gradlew bootRun
```

Или явно:

```bash
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

Параметры БД можно переопределить:

- `DBHOST`
- `DBPORT`
- `DBNAME`
- `SCHEMANAME`
- `DB_USERNAME`
- `DB_PASSWORD`
- `TOMCAT_MAX_THREADS`
- `SPRING_JPA_SHOW_SQL`

Формат JDBC соответствует требованиям LAB7:

```properties
spring.datasource.url=jdbc:postgresql://${DBHOST:hl12.zil}:${DBPORT:5432}/${DBNAME:hl5}?currentSchema=${SCHEMANAME:hl5}
```

Swagger UI доступен по адресу:

```text
http://localhost:8080/swagger-ui.html
```

## Запуск без БД

```bash
./gradlew bootRun --args='--spring.profiles.active=memory'
```

Этот режим использует `HashMap`-репозитории и стартовые данные из `MemoryDataInitializer`.
Он нужен только для быстрой проверки бизнес-логики без инфраструктуры.

## API

Рейсы:

```text
GET    /flights
GET    /flights/{id}
GET    /flights/availability
GET    /flights/{flightId}/availability
GET    /flights/free-seats?destination={destination}&date={yyyy-MM-dd}
GET    /flights/number/{flightNumber}/availability
POST   /flights
PUT    /flights/{id}
DELETE /flights/{id}
```

Пассажиры:

```text
GET    /passengers
GET    /passengers/{id}
POST   /passengers
PUT    /passengers/{id}
DELETE /passengers/{id}
```

Бронирования:

```text
GET    /bookings
GET    /bookings?flightId={flightId}
GET    /bookings/{id}
POST   /bookings
DELETE /bookings/{id}
```

Служебное:

```text
DELETE /clear
```

`/clear` нужен для подготовки данных перед нагрузочными тестами.

## Примеры запросов

Создать рейс:

```bash
curl -X POST http://localhost:8080/flights \
  -H "Content-Type: application/json" \
  -d '{
    "flightNumber": "SU777",
    "destination": "Novosibirsk",
    "departureDate": "2026-06-15",
    "capacity": 10
  }'
```

Создать пассажира:

```bash
curl -X POST http://localhost:8080/passengers \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Мария Смирнова",
    "passportData": "5555 666777",
    "contacts": "maria@example.com"
  }'
```

Создать бронирование:

```bash
curl -X POST http://localhost:8080/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "flightId": 1,
    "passengerId": 1,
    "serviceClass": "ECONOMY",
    "seat": "5A"
  }'
```

Проверить свободные места:

```bash
curl "http://localhost:8080/flights/free-seats?destination=Moscow&date=2026-06-10"
```

Проверить бизнес-ошибку:

```bash
curl -X POST http://localhost:8080/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "flightId": 1,
    "passengerId": 1,
    "serviceClass": "ECONOMY",
    "seat": "1A"
  }'
```

Ожидаемо вернётся ошибка, потому что место `1A` уже занято стартовыми данными.

## Где что находится

- `controller` - REST endpoints;
- `controller/dto` - JSON-запросы и ответы;
- `service` - бизнес-логика;
- `repository` - общий контракт хранения;
- `repository/jpa` - PostgreSQL-реализация;
- `repository/memory` - in-memory реализация;
- `model` - JPA-сущности;
- `config` - вспомогательная конфигурация memory-режима;
- `src/main/resources/db/migration` - Flyway migrations;
- `Dockerfile` - сборка контейнера приложения;
- `docker-compose.yml` - приложение + PostgreSQL;
- `k6` - нагрузочные сценарии;
- `scripts` - Python-утилиты.

## Что показывать преподавателю

1. `application.properties` и `application-postgres.properties`: основной профиль `postgres`.
2. `model`: сущности и связи.
3. `repository/jpa`: Spring Data JPA.
4. `service`: проверки бизнес-правил.
5. `controller`: REST API.
6. `db/migration`: Flyway DDL и DML.
7. `Dockerfile` и `docker-compose.yml`: контейнерный стенд.
8. Postman или curl: use [`postman/`](/Users/sergejmegeran/Desktop/Высоконагруженные%20вычислительные%20системы/Airline_tickets/postman) for the memory and postgres collections, or `requests.http` for `GET /flights`, `GET /flights/free-seats`, `POST /bookings`.
9. k6: `k6/load-profile.js`, единый сценарий `k6/lab4-load-test.js`, график из `scripts/plot_k6_results.py`.

## Проверки

```bash
./gradlew test --no-daemon
node --check k6/load-profile.js
node --check k6/lab4-load-test.js
python3 -m py_compile scripts/seed_data.py scripts/plot_k6_results.py
```

## Нагрузочное тестирование

Для LAB6 добавлен отдельный сценарий с постоянной нагрузкой и заданным соотношением запись/чтение:

```bash
k6 run \
  --out json=reports/lab6-0.5cpu-5-95.json \
  -e BASE_URL=http://localhost:8080 \
  -e VUS=20 \
  -e DURATION=2m \
  -e WRITE_PERCENT=5 \
  -e READ_PERCENT=95 \
  k6/lab6-ratio-test.js
```

График по экспериментам с разным CPU строится так:

```bash
python3 scripts/plot_lab6_cpu_results.py \
  --result 5/95@0.5=reports/lab6-0.5cpu-5-95.json \
  --result 5/95@1.0=reports/lab6-1.0cpu-5-95.json \
  --result 50/50@0.5=reports/lab6-0.5cpu-50-50.json \
  --result 95/5@0.5=reports/lab6-0.5cpu-95-5.json \
  --output reports/lab6-cpu-response-time.png
```

Для LAB7 вынесен отдельный compose-файл БД-узла:

```bash
docker compose -f docker-compose.db.yml up -d
```

Он поднимает:

- `postgres` c `command: postgres -c max_connections=1000`
- `pgadmin` на `http://localhost:5050`

Установить зависимости Python:

```bash
pip install -r requirements-lab5.txt
```

Сценарий k6:

```bash
k6 run --out json=reports/k6-lab4-result.json k6/lab4-load-test.js
```

Внутри сценария:

- `POST /flights` нагружает создание простой сущности `Flight`;
- `GET /flights/free-seats?destination=Moscow&date=2026-06-10` проверяет дополнительный endpoint статистики;
- профиль нагрузки реализует тест удвоения: `10 -> 20 -> 40 -> 80 VUs` с минутным плато на каждом уровне;
- итоговый график строится как зависимость среднего времени ответа (`avg`) от нагрузки (`vus`).

Мониторинг через Grafana:

```bash
docker compose -f docker-compose-monitoring.yml up -d
k6 run --out influxdb=http://localhost:8086/k6 --out json=reports/k6-lab4-result.json k6/lab4-load-test.js
```

График:

```bash
python3 scripts/plot_k6_results.py --input reports/k6-lab4-result.json --output reports/k6-lab4-response-time.png
```

Заполнение данных перед нагрузкой:

```bash
python3 scripts/seed_data.py --count 500 --endpoint bookings
```
