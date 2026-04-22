import http from 'k6/http';
import { check, sleep } from 'k6';
import { baseUrl, options as profileOptions } from './load-profile.js';

export const options = profileOptions;

export default function () {
  const flightsResponse = http.get(`${baseUrl}/flights`);
  check(flightsResponse, {
    'GET /flights status is 200': (response) => response.status === 200,
  });

  const freeSeatsResponse = http.get(`${baseUrl}/flights/free-seats?destination=Moscow`);
  check(freeSeatsResponse, {
    'GET /flights/free-seats status is 200': (response) => response.status === 200,
  });

  sleep(1);
}
