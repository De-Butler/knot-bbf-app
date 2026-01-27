# ---------- build stage ----------
FROM gradle:7.6-jdk8 AS builder
WORKDIR /app

# 먼저 전체 복사
COPY . .

# ✅ 권한은 "전체 복사 이후"에 부여해야 함
RUN chmod +x ./gradlew

RUN ./gradlew clean build -x test --no-daemon

# ---------- runtime stage ----------
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]