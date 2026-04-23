import http from 'k6/http';
import { check, sleep } from 'k6';

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';
const vus = Number(__ENV.VUS || 20);
const duration = __ENV.DURATION || '2m';
const writeShare = Number(__ENV.WRITE_PERCENT || 5);
const readShare = Number(__ENV.READ_PERCENT || 95);
const thinkTime = Number(__ENV.SLEEP_SECONDS || 0.2);

if (writeShare + readShare !== 100) {
    throw new Error(`WRITE_PERCENT + READ_PERCENT must be 100, got ${writeShare + readShare}`);
}

export const options = {
    scenarios: {
        mixed_ratio: {
            executor: 'constant-vus',
            vus,
            duration,
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['avg<2000'],
    },
};

const cities = ['Sochi', 'Istanbul', 'Dubai', 'Tbilisi', 'Yerevan', 'Belgrade', 'Almaty'];
const departureDates = ['2026-06-10', '2026-06-11', '2026-06-12', '2026-06-13'];

export default function () {
    if (Math.random() * 100 < writeShare) {
        createFlight();
    } else {
        readAvailability();
    }

    sleep(thinkTime);
}

function createFlight() {
    const city = cities[Math.floor(Math.random() * cities.length)];
    const departureDate = departureDates[Math.floor(Math.random() * departureDates.length)];
    const response = http.post(
        `${baseUrl}/flights`,
        JSON.stringify({
            flightNumber: `CPU-${Math.random().toString(36).slice(2, 8).toUpperCase()}`,
            destination: city,
            departureDate,
            capacity: 100,
        }),
        {
            headers: { 'Content-Type': 'application/json' },
            tags: { mix: `${writeShare}/${readShare}`, action: 'write' },
        },
    );

    check(response, { 'POST /flights created': (res) => res.status === 201 });
}

function readAvailability() {
    const response = http.get(`${baseUrl}/flights/availability`, {
        tags: { mix: `${writeShare}/${readShare}`, action: 'read' },
    });

    check(response, { 'GET /flights/availability ok': (res) => res.status === 200 });
}
