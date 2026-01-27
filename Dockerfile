# ---------- build stage ----------
FROM gradle:7.6-jdk8 AS builder
WORKDIR /app



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
