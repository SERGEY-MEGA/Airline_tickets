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
- LAB5: данные для нагрузки создаются отдельным скриптом, нагрузка запускается через k6.

## Что отвечать на доп. задачки

Если просят поменять объём данных: изменить `--count`.
Если просят тестировать другой endpoint: изменить `--endpoint` или k6-сценарий.
Если просят очистить данные: `DELETE /clear`.
Если просят показать график: запустить k6 с `--out json=...`, потом `plot_k6_results.py`.
