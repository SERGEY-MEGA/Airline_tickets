export const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';

// LAB4/LAB5: учебный профиль нагрузки для k6.
// executor ramping-vus постепенно увеличивает и снижает число виртуальных пользователей.
export const options = {
  scenarios: {
    demo_load: {
      executor: 'ramping-vus',
      stages: [
        { duration: '30s', target: 5 },
        { duration: '1m', target: 10 },
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<1000'],
  },
};
