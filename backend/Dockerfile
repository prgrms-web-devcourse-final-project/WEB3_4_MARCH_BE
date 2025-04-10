# 베이스 이미지 설정 (예: openjdk:21-jdk)
FROM openjdk:21-jdk

# 작업 디렉토리 생성
WORKDIR /app

COPY src/main/resources/application-server.yml /app/config/application-server.yml

# 빌드된 jar 파일을 컨테이너로 복사 (예: backend-0.0.1-SNAPSHOT.jar)
COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너 시작 시 jar 파일을 실행 (Spring profile은 환경변수로 전달)
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.additional-location=file:/app/config/application-server.yml"]
