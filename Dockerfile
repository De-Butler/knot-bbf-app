# ---------- build stage ----------
FROM eclipse-temurin:8-jdk AS builder
WORKDIR /app

# 캐시 효율: 의존성 파일 먼저
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
RUN chmod +x /app/gradlew

# 소스 복사 후 빌드
COPY . /app
RUN ./gradlew clean build -x test --no-daemon

# ---------- runtime stage ----------
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]