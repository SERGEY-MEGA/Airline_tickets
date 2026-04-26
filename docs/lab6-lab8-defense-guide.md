# LAB6-LAB8 Defense Guide

## Personal Data

- Student: Sergey Megeryan, number 5
- VM: `ssh -p 2305 hl@hlssh.zil.digital`
- DB: `hl5`
- DB user: `hl5`
- DB password: `pass_5`
- DB schema: `hl5`
- Harbor: `10.60.3.11:8888`
- Harbor user: `admin`
- Harbor password: `hl_labs`

## LAB6 Commands

```bash
ssh -p 2305 hl@hlssh.zil.digital
sudo apt update
sudo apt upgrade -y
sudo apt install -y git
ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa -N ""
cat ~/.ssh/id_rsa.pub
cd ~/Airline_tickets
docker compose up -d --build
docker compose ps
curl http://localhost:8080/flights
```

Local tunnel from Mac:

```bash
ssh -N -L 8080:localhost:8080 -p 2305 hl@hlssh.zil.digital
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## LAB7 Commands

On DB node:

```bash
export DBNAME=hl5
export DB_USERNAME=hl5
export DB_PASSWORD=pass_5
docker compose -f docker-compose.db.yml up -d
```

Application DB connection:

```properties
spring.datasource.url=jdbc:postgresql://${DBHOST:hl12.zil}:${DBPORT:5432}/${DBNAME:hl5}?currentSchema=${SCHEMANAME:hl5}
```

Do not connect as `postgres`; use `hl5`.

## LAB8 Commands

Run both services:

```bash
cd ~/Airline_tickets
ADDITIONAL_SERVICE_CPUS=0.5 docker compose -f docker-compose.lab8.yml up -d --build
docker compose -f docker-compose.lab8.yml ps
curl http://localhost:8081/stats/availability
```

Publish images to Harbor:

```bash
docker login 10.60.3.11:8888
docker compose -f docker-compose.lab8.yml build
docker push 10.60.3.11:8888/library/airline-tickets-app:latest
docker push 10.60.3.11:8888/library/airline-tickets-additional-service:latest
```

LAB8 load test, server-server:

```bash
cd ~/Airline_tickets_additional_service
k6 run --out json=reports/lab8-0.5cpu.json \
  -e BASE_URL=http://localhost:8081 \
  -e VUS=20 \
  -e DURATION=2m \
  k6/lab8-additional-service-test.js
```

For CPU 1.0:

```bash
cd ~/Airline_tickets
ADDITIONAL_SERVICE_CPUS=1.0 docker compose -f docker-compose.lab8.yml up -d --build
cd ~/Airline_tickets_additional_service
k6 run --out json=reports/lab8-1.0cpu.json \
  -e BASE_URL=http://localhost:8081 \
  -e VUS=20 \
  -e DURATION=2m \
  k6/lab8-additional-service-test.js
```

Build chart:

```bash
python3 scripts/plot_lab8_cpu_results.py \
  --result 0.5=reports/lab8-0.5cpu.json \
  --result 1.0=reports/lab8-1.0cpu.json \
  --output reports/lab8-cpu-response-time.png
```

## Defense Text

LAB6: I configured the VM, updated the OS, added SSH key access, installed Git, cloned the project, logged in to the registry, and deployed the app with Docker Compose. The app container has explicit CPU and memory limits. Database connection, Tomcat thread count, and Hibernate SQL logging are passed through environment variables. I check the REST API through Swagger UI and access it from my laptop through an SSH tunnel.

LAB7: I moved the database to a dedicated DB node. PostgreSQL and pgAdmin are started by Docker Compose. PostgreSQL is started with `postgres -c max_connections=1000`. The app uses `hl12.zil` as the main DB host and connects to my database `hl5` with user `hl5`; I do not use the `postgres` superuser.

LAB8: I moved the additional flight availability/free-seats logic into a separate microservice. The additional service calls the main CRUD service through `RestTemplate`, receives flights and bookings over HTTP, and joins them in Java. It does not query the database directly. Both services run from one compose file, and CPU for the additional service is set explicitly to `0.5` or `1.0`. I run the same style of server-server k6 experiment as in LAB6 and build a CPU versus response time chart.
