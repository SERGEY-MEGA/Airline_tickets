import http from 'k6/http';
import { check, sleep } from 'k6';
import { baseUrl, options as profileOptions } from './load-profile.js';

export const options = profileOptions;

export default function () {
  const suffix = `${__VU}-${__ITER}-${Date.now()}`;

  // LAB4: нагружаем создание простой сущности Flight, у которой нет ссылок на другие таблицы.
  const createFlightResponse = http.post(
    `${baseUrl}/flights`,
    JSON.stringify({
      flightNumber: `K6${suffix}`,
      destination: 'Moscow',
      departureDate: '2026-06-10',
      capacity: 100,
    }),
    {
      headers: {
        'Content-Type': 'application/json',
      },
    },
  );

  check(createFlightResponse, {
    'POST /flights status is 201': (response) => response.status === 201,
  });

  // Дополнительный endpoint из бизнес-постановки: статистика свободных мест.
  const statsResponse = http.get(`${baseUrl}/flights/free-seats?destination=Moscow&date=2026-06-10`);
  check(statsResponse, {
    'GET /flights/free-seats status is 200': (response) => response.status === 200,
  });

  sleep(1);
}
