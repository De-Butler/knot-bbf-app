# ---------- build stage ----------
FROM eclipse-temurin:8-jdk-alpine AS builder
WORKDIR /app

# gradlew 실행에 필요한 패키지 (bash 필수인 경우 많음)
RUN apk add --no-cache bash

# 캐시 효율: gradle wrapper/설정 먼저
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle* settings.gradle* ./

RUN chmod +x ./gradlew

# 소스 복사 후 빌드
COPY . .
RUN ./gradlew clean build -x test --no-daemon

# ---------- runtime stage ----------
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
