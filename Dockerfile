# 빌드 스테이지
FROM gradle:jdk21-jammy AS builder
WORKDIR /build
COPY . .
RUN gradle build --no-daemon

# 실행 스테이지
FROM openjdk:21-jdk-slim
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
COPY --from=builder /build/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]