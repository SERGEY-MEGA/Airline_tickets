-- DML migration.
-- Этот файл добавляет тестовые данные для Postman и демонстрации преподавателю.
-- DML означает Data Manipulation Language: вставка и изменение данных.

-- Тестовые рейсы.
INSERT INTO flights (id, flight_number, destination, departure_date, capacity) VALUES
    (1, 'SU500', 'Moscow', '2026-06-10', 6),
    (2, 'DP610', 'Saint Petersburg', '2026-06-10', 4),
    (3, 'S7200', 'Sochi', '2026-06-11', 5)
ON CONFLICT (id) DO NOTHING;

-- Тестовые пассажиры.
INSERT INTO passengers (id, full_name, passport_data, contacts) VALUES
    (1, 'Иван Иванов', '1234 567890', 'ivanov@example.com'),
    (2, 'Анна Петрова', '4321 987654', '+7-999-123-45-67'),
    (3, 'Пётр Сидоров', '7777 111222', 'sidorov@example.com')
ON CONFLICT (id) DO NOTHING;

-- Тестовые бронирования.
INSERT INTO bookings (id, flight_id, passenger_id, service_class, seat) VALUES
    (1, 1, 1, 'ECONOMY', '1A'),
    (2, 2, 2, 'BUSINESS', '2B'),
    (3, 3, 3, 'ECONOMY', '3C')
ON CONFLICT (id) DO NOTHING;

-- Сдвигаем автоинкремент, чтобы новые записи получали id после тестовых данных.
ALTER TABLE flights ALTER COLUMN id RESTART WITH 10;
ALTER TABLE passengers ALTER COLUMN id RESTART WITH 10;
ALTER TABLE bookings ALTER COLUMN id RESTART WITH 10;
