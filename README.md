# Авиакасса

Учебный проект по дисциплине **«Высоконагруженные вычислительные системы»**.

Проект реализует простую систему бронирования авиабилетов с двумя режимами работы:

- `LAB1` — хранение данных в памяти через `static HashMap`
- `LAB2` — хранение данных в PostgreSQL через Spring Data JPA
- `LAB3` — контейнеризация приложения и БД через Docker Compose, схема и данные через Flyway migrations

На момент адаптации в удалённом репозитории была доступна ветка `main`, поэтому именно она использована как фактическая основа вместо указанной в задании `feature/spring-boot-test`.

## Что делает проект

Система поддерживает три основные сущности:

- `Flight` — рейс
- `Passenger` — пассажир
- `Booking` — бронирование

Класс обслуживания хранится в `ServiceClass`:

- `ECONOMY`
- `BUSINESS`
- `FIRST`

Реализованы бизнес-правила:

- нельзя забронировать место на несуществующий рейс
- нельзя забронировать место несуществующему пассажиру
- нельзя занять уже занятое место на рейсе
- нельзя превысить вместимость рейса
- можно получить остаток свободных мест по направлению и дате
- можно получить список бронирований по конкретному рейсу

## Структура проекта

```text
src/main/java/digital/zil/hl/module1
├── config
├── controller
│   ├── dto
│   └── exception
├── model
├── repository
│   ├── memory
│   └── jpa
└── service
```

### Слои

- `controller` — REST API
- `service` — бизнес-логика и базовая валидация
- `repository` — работа с хранилищем
- `model` — доменные сущности

## LAB1

### Как работает

- активный профиль по умолчанию: `lab1`
- репозитории работают на `static HashMap`
- стартовые тестовые данные загружаются через `Lab1DataInitializer`

### Запуск LAB1

```bash
./gradlew bootRun
```

Приложение стартует на:

```text
http://localhost:8080
```

### Что уже есть после запуска LAB1

Будут созданы тестовые данные:

- рейсы
- пассажиры
- бронирования

Поэтому сразу после запуска можно открывать `GET /flights`, `GET /passengers`, `GET /bookings`.

## LAB2

### Как работает

- используется профиль `lab2`
- репозитории переключаются на Spring Data JPA
- данные хранятся в PostgreSQL
- схема создаётся Hibernate
- тестовые данные подгружаются из `src/main/resources/data.sql`

### Запуск PostgreSQL

Если Docker сейчас не установлен, этот шаг нужно выполнить после повторной установки Docker Desktop.
Конфигурация уже подготовлена: отдельный контейнер PostgreSQL описан в `docker-compose.yml`.

```bash
docker compose up -d postgres
```

Проверка контейнера:

```bash
docker compose ps
```

Остановка:

```bash
docker compose down
```

Если Docker не хочется использовать для LAB2, можно поднять локальный PostgreSQL вручную и создать БД
`airline_tickets`. Тогда оставьте стандартные параметры подключения или задайте `DB_URL`,
`DB_USERNAME`, `DB_PASSWORD`.

### Запуск LAB2

```bash
./gradlew bootRun --args='--spring.profiles.active=lab2'
```

### Параметры подключения к БД

По умолчанию используются значения:

- `DB_URL=jdbc:postgresql://localhost:5432/airline_tickets`
- `DB_USERNAME=postgres`
- `DB_PASSWORD=postgres`

При необходимости их можно переопределить через переменные окружения.

## LAB3

### Как работает

- приложение запускается в Docker-контейнере
- PostgreSQL запускается в Docker-контейнере
- профиль приложения: `lab3`
- схема БД создаётся из `src/main/resources/db/migration/V1__create_airline_schema.sql`
- тестовые данные добавляются из `src/main/resources/db/migration/V2__insert_test_data.sql`
- миграции запускает Flyway

### Запуск LAB3

Перед запуском проверьте, что Docker Desktop установлен и запущен.

Обычный запуск на порту `8080`:

```bash
docker compose up -d --build
```

Если порт `8080` занят, можно запустить на другом внешнем порту, например `8083`:

```bash
APP_PORT=8083 docker compose up -d --build
```

Проверка контейнеров:

```bash
docker compose ps
```

Проверка Flyway-миграций:

```bash
docker compose exec -T postgres psql -U postgres -d airline_tickets -c "select installed_rank, version, description, success from flyway_schema_history order by installed_rank;"
```

Остановка стенда:

```bash
docker compose down
```

Если нужно начать демонстрацию с чистой БД, можно удалить volume PostgreSQL:

```bash
docker compose down -v
docker compose up -d --build
```

Для обычной сдачи чаще достаточно `docker compose down`, чтобы не удалять данные между перезапусками.

## API

### Рейсы

```text
GET    /flights
GET    /flights/{id}
GET    /flights/{flightId}/availability
GET    /flights/availability
GET    /flights/free-seats?destination={destination}&date={yyyy-MM-dd}
GET    /flights/free-seats?destination={destination}
GET    /flights/free-seats?date={yyyy-MM-dd}
GET    /flights/number/{flightNumber}/availability
POST   /flights
PUT    /flights/{id}
DELETE /flights/{id}
```

### Пассажиры

```text
GET    /passengers
GET    /passengers/{id}
POST   /passengers
PUT    /passengers/{id}
DELETE /passengers/{id}
```

### Бронирования

```text
GET    /bookings
GET    /bookings?flightId={flightId}
GET    /bookings/{id}
POST   /bookings
DELETE /bookings/{id}
```

## Примеры HTTP-запросов

### Получить все рейсы

```bash
curl http://localhost:8080/flights
```

### Создать рейс

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

### Создать пассажира

```bash
curl -X POST http://localhost:8080/passengers \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Мария Смирнова",
    "passportData": "5555 666777",
    "contacts": "maria@example.com"
  }'
```

### Создать бронирование

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

### Получить остаток свободных мест по направлению и дате

```bash
curl "http://localhost:8080/flights/free-seats?destination=Moscow&date=2026-06-10"
```

### Получить остаток свободных мест только по направлению

```bash
curl "http://localhost:8080/flights/free-seats?destination=Moscow"
```

### Получить остаток свободных мест только по дате

```bash
curl "http://localhost:8080/flights/free-seats?date=2026-06-10"
```

### Получить бронирования по рейсу

```bash
curl "http://localhost:8080/bookings?flightId=1"
```

## Что показать преподавателю

### Для LAB1

Показать:

- что используется профиль `lab1`
- что репозитории лежат в `repository/memory`
- что данные хранятся в `static HashMap`
- что API работает без БД
- что есть стартовые тестовые данные
- что ограничение по местам и проверка занятых мест вынесены в слой сервисов и репозиториев

Удобная демонстрация:

1. `GET /flights`
2. `GET /passengers`
3. `GET /bookings`
4. `GET /flights/free-seats?...`
5. `POST /bookings`
6. повторный `POST /bookings` на то же место, чтобы показать ошибку

### Для LAB2

Показать:

- `docker-compose.yml`
- профиль `lab2`
- JPA-аннотации в моделях
- репозитории в `repository/jpa`
- `data.sql`
- запуск через PostgreSQL
- тот же API без изменения бизнес-смысла

Удобная демонстрация:

1. `docker compose up -d postgres`
2. `./gradlew bootRun --args='--spring.profiles.active=lab2'`
3. `GET /flights`
4. `GET /bookings`
5. `GET /flights/free-seats?...`
6. `POST /bookings`

### Для LAB3

Показать:

- `Dockerfile`
- `docker-compose.yml`
- профиль `lab3`
- папку `src/main/resources/db/migration`
- таблицу `flyway_schema_history`
- что приложение и PostgreSQL работают как два контейнера

Удобная демонстрация:

1. `docker compose up -d --build`
2. `docker compose ps`
3. открыть `Dockerfile`
4. открыть `src/main/resources/db/migration`
5. выполнить `GET /flights`
6. выполнить `GET /flights/free-seats?destination=Moscow&date=2026-06-10`
7. показать `flyway_schema_history`

## Проверка проекта

Запуск тестов:

```bash
./gradlew test --no-daemon
```

Коллекция Postman лежит в файле `AirlineTickets.postman_collection.json`.
Для быстрой проверки из IntelliJ IDEA можно открыть `requests.http`.

## Что открыть в IntelliJ IDEA

Для LAB2:

- `src/main/resources/application-lab2.properties`
- `src/main/resources/data.sql`
- `src/main/java/digital/zil/hl/module1/model`
- `src/main/java/digital/zil/hl/module1/repository/jpa`
- `src/main/java/digital/zil/hl/module1/service`
- `src/main/java/digital/zil/hl/module1/controller`

Для LAB3:

- `Dockerfile`
- `docker-compose.yml`
- `src/main/resources/application-lab3.properties`
- `src/main/resources/db/migration/V1__create_airline_schema.sql`
- `src/main/resources/db/migration/V2__insert_test_data.sql`

## Короткий сценарий защиты

LAB2:

1. Запустить PostgreSQL: `docker compose up -d postgres`.
2. Запустить приложение: `./gradlew bootRun --args='--spring.profiles.active=lab2'`.
3. В Postman выполнить `GET /flights`, `GET /passengers`, `GET /bookings`.
4. Выполнить `GET /flights/free-seats?destination=Moscow&date=2026-06-10`.
5. Выполнить `POST /bookings` с новым местом, например `5A`.
6. Повторить бронь на уже занятое место `1A`, чтобы показать бизнес-ошибку.

LAB3:

1. Запустить стенд: `docker compose up -d --build`.
2. Проверить контейнеры: `docker compose ps`.
3. Показать `Dockerfile` и `docker-compose.yml`.
4. Показать миграции `V1__create_airline_schema.sql` и `V2__insert_test_data.sql`.
5. Выполнить в Postman те же запросы, что и для LAB2.
6. Показать таблицу `flyway_schema_history` командой из раздела LAB3.

## Полезные замечания

- в `LAB1` БД не используется
- в `LAB2` бизнес-логика сохранена, меняется только способ хранения
- в `LAB3` бизнес-логика тоже не меняется, добавляется контейнеризация и миграции БД
- проект специально сделан без security, frontend и лишних библиотек
- валидация базовая и учебно-понятная, без перегрузки архитектуры
