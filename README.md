# Система Бронирования Авиабилетов — Лабораторная работа 1

Дисциплина: **Высоконагруженные вычислительные системы**

## Стек
- Java 25 · Spring Boot 4 · Gradle
- Хранилище: статические коллекции (`HashMap`) в памяти

---

## Что реализовано

| Сущность | Поля |
|---|---|
| **Flight** (Рейс) | `id`, `flightNumber`, `destination`, `departureDate`, `capacity` |
| **Passenger** (Пассажир) | `id`, `fullName`, `passportData`, `contacts` |
| **Booking** (Бронирование) | `id`, `flightId`, `passengerId`, `serviceClass`, `seat` |

### Структура проекта
```
src/main/java/digital/zil/hl/module1/
├── model/          — Flight, Passenger, Booking (POJO, без Lombok)
├── repository/     — FlightRepository, PassengerRepository, BookingRepository
│                     (один класс на сущность, static HashMap, без интерфейсов)
├── service/        — FlightService, PassengerService, BookingService
└── controller/     — FlightController, PassengerController, BookingController
    └── exeption/   — AirlineException, GlobalExceptionHandler
```

---

## API

### Рейсы
```
GET    /flights                                       — список всех рейсов
GET    /flights/{id}                                  — рейс по ID
GET    /flights/{flightId}/availability               — остаток мест для рейса
POST   /flights                                       — создать рейс
PUT    /flights/{id}                                  — обновить рейс (в т.ч. номер)
DELETE /flights/{id}                                  — удалить рейс
```

### Пассажиры
```
GET    /passengers                  — список пассажиров
GET    /passengers/{id}             — пассажир по ID
POST   /passengers                  — зарегистрировать пассажира
PUT    /passengers/{id}             — обновить данные пассажира
DELETE /passengers/{id}             — удалить пассажира
```

### Бронирования
```
GET    /bookings                    — список бронирований
GET    /bookings/{id}               — бронирование по ID
POST   /bookings                    — забронировать место
DELETE /bookings/{id}               — отменить бронирование
```

---

## Запуск

```bash
./gradlew bootRun
```
Приложение запустится на `http://localhost:8080`.

**Запуск тестов:**
```bash
./gradlew test --no-daemon
```

---

## Демонстрация (curl-команды)

### 1. Создать рейс
```bash
curl -X POST http://localhost:8080/flights \
  -H "Content-Type: application/json" \
  -d '{"flightNumber":"SU100","destination":"Moscow","departureDate":"2026-03-20","capacity":2}'
```

### 2. Изменить номер рейса SU100 → SU200
```bash
# PUT /flights/{id} — полное обновление рейса с ID = 1
curl -X PUT http://localhost:8080/flights/1 \
  -H "Content-Type: application/json" \
  -d '{"flightNumber":"SU200","destination":"Moscow","departureDate":"2026-03-20","capacity":2}'
```
> Метод `put()` в `FlightRepository` заменяет объект целиком, сохраняя тот же `id`.

### 3. Зарегистрировать пассажира
```bash
curl -X POST http://localhost:8080/passengers \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Иван Иванов","passportData":"1234 567890","contacts":"test@test.com"}'
```

### 4. Забронировать место
```bash
curl -X POST http://localhost:8080/bookings \
  -H "Content-Type: application/json" \
  -d '{"flightId":1,"passengerId":1,"serviceClass":"Economy","seat":"1A"}'
```

### 5. Проверить остаток свободных мест
```bash
curl "http://localhost:8080/flights/1/availability"
```
Ожидаемый ответ включает поля `capacity`, `bookedSeats` и `availableSeats`.

---

## Ключевой момент для "Высоконагруженных систем"

**Проблема:** Если два пользователя одновременно зайдут на последнее место или превысят вместимость рейса — без защиты оба запроса "пройдут" в промежутке между проверкой и записью (Race Condition).

**Решение (`BookingRepository.java`):**
```java
public synchronized Booking save(Booking booking, int flightCapacity) {
    // Обе проверки выполняются атомарно — пока один поток внутри,
    // все остальные ждут у входа.
    if (countByFlightId(booking.getFlightId()) >= flightCapacity) { ... }
    if (isSeatTaken(booking.getFlightId(), booking.getSeat()))     { ... }
    bookings.put(...);
    return booking;
}
```

Аналог в реальной БД — транзакция с `SELECT ... FOR UPDATE` или уровень изоляции `SERIALIZABLE`.

**Тест** (`BookingServiceConcurrencyTest`): 15 потоков одновременно бронируют места на рейс вместимостью 10.
Ровно 10 проходят, 5 получают `AirlineException`. Overoverbooking исключён.
