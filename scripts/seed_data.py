#!/usr/bin/env python3
import argparse
import math
import sys
from datetime import date, timedelta

import requests
from faker import Faker


fake = Faker("ru_RU")


def parse_args():
    # Аргументы специально простые: ими удобно менять объём данных прямо перед k6.
    parser = argparse.ArgumentParser(
        description="Fill Airline Tickets REST API with generated data before k6 load tests."
    )
    parser.add_argument("--count", type=int, default=500, help="Number of target objects to create.")
    parser.add_argument(
        "--endpoint",
        default="bookings",
        help="Target endpoint: flights, passengers, bookings or all.",
    )
    parser.add_argument("--base-url", default="http://localhost:8080", help="REST API base URL.")
    return parser.parse_args()


def request_json(method, base_url, path, **kwargs):
    # Общая обёртка для REST-вызовов: если сервис вернул ошибку, сразу останавливаем генерацию.
    response = requests.request(method, f"{base_url}{path}", timeout=10, **kwargs)
    if response.status_code >= 400:
        raise RuntimeError(f"{method} {path} failed: {response.status_code} {response.text}")
    if response.status_code == 204 or not response.text:
        return None
    return response.json()


def clear_data(base_url):
    # Данные очищаются через приложение, а не прямым SQL, чтобы скрипт не зависел от БД.
    request_json("DELETE", base_url, "/clear")


def create_flights(base_url, count, capacity):
    # Рейсы создаются первыми: бронирование не может существовать без рейса.
    flights = []
    destinations = ["Moscow", "Saint Petersburg", "Sochi", "Kazan", "Novosibirsk", "Yekaterinburg"]
    for index in range(1, count + 1):
        payload = {
            "flightNumber": f"HL{index:05d}",
            "destination": destinations[index % len(destinations)],
            "departureDate": (date.today() + timedelta(days=1 + index % 30)).isoformat(),
            "capacity": capacity,
        }
        flights.append(request_json("POST", base_url, "/flights", json=payload))
    return flights


def create_passengers(base_url, count):
    # Пассажиры создаются отдельно, чтобы затем связать каждого с бронированием.
    passengers = []
    for _ in range(count):
        payload = {
            "fullName": fake.name(),
            "passportData": fake.bothify(text="#### ######"),
            "contacts": fake.email(),
        }
        passengers.append(request_json("POST", base_url, "/passengers", json=payload))
    return passengers


def seat_name(index):
    # Простая генерация мест: 1A, 1B, 1C... Этого достаточно для учебной нагрузки.
    letters = "ABCDEF"
    row = index // len(letters) + 1
    letter = letters[index % len(letters)]
    return f"{row}{letter}"


def create_bookings(base_url, count):
    # Для bookings скрипт сам создаёт зависимые таблицы: рейсы и пассажиров.
    flight_count = max(1, min(count, 50))
    capacity = max(10, math.ceil(count / flight_count) + 5)
    flights = create_flights(base_url, flight_count, capacity)
    passengers = create_passengers(base_url, count)
    seats_by_flight = {flight["id"]: 0 for flight in flights}

    bookings = []
    for index in range(count):
        flight = flights[index % len(flights)]
        passenger = passengers[index]
        flight_id = flight["id"]
        seat_index = seats_by_flight[flight_id]
        seats_by_flight[flight_id] += 1

        payload = {
            "flightId": flight_id,
            "passengerId": passenger["id"],
            "serviceClass": "ECONOMY",
            "seat": seat_name(seat_index),
        }
        bookings.append(request_json("POST", base_url, "/bookings", json=payload))
    return bookings


def main():
    args = parse_args()
    if args.count <= 0:
        raise SystemExit("--count must be greater than zero")

    endpoint = args.endpoint.strip("/").lower()
    if endpoint not in {"flights", "passengers", "bookings", "all"}:
        raise SystemExit("--endpoint must be one of: flights, passengers, bookings, all")

    clear_data(args.base_url)

    if endpoint == "flights":
        created = create_flights(args.base_url, args.count, capacity=100)
    elif endpoint == "passengers":
        created = create_passengers(args.base_url, args.count)
    else:
        created = create_bookings(args.base_url, args.count)

    print(f"Created {len(created)} {endpoint} objects at {args.base_url}")


if __name__ == "__main__":
    try:
        main()
    except requests.RequestException as exc:
        print(f"HTTP error: {exc}", file=sys.stderr)
        raise SystemExit(1)
    except RuntimeError as exc:
        print(str(exc), file=sys.stderr)
        raise SystemExit(1)
