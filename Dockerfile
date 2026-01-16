# Java 8
FROM eclipse-temurin:8-jdk-alpine

# 작업 폴더를 생성
WORKDIR /app

# 실행 파일(.jar)을 컨테이너 안으로 복사
# 주의: build/libs 안에 있는 jar 파일을 가져옴
COPY build/libs/*.jar app.jar

# 컨테이너가 시작되면 자바 프로그램을 실행
ENTRYPOINT ["java", "-jar", "app.jar"]