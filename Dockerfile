# Dockerfile для контейнеризации Spring Boot приложения.
# Используется multi-stage build:
# 1) первый stage собирает jar;
# 2) второй stage запускает уже готовое приложение.

FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Копируем Gradle wrapper и исходный код, чтобы собрать приложение внутри Docker.
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src

# Собираем исполняемый Spring Boot jar.
RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# В runtime-образ кладём только готовый jar, без исходников и Gradle.
COPY --from=build /app/build/libs/*.jar app.jar

# Приложение внутри контейнера слушает порт 8080.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
