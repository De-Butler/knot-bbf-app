# ---------- build stage ----------
FROM eclipse-temurin:8-jdk AS builder
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends bash ca-certificates \
  && rm -rf /var/lib/apt/lists/*

# gradle wrapper/설정 먼저 (캐시 효율)
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle* settings.gradle* ./
RUN chmod +x ./gradlew

# 소스 복사 후 빌드
COPY . .
RUN ./gradlew clean build -x test --no-daemon

# ---------- runtime stage ----------
FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]