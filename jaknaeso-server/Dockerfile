# 빌드 스테이지
FROM gradle:jdk21-jammy AS builder
WORKDIR /build

COPY gradle ./gradle
COPY build.gradle.kts ./
COPY settings.gradle.kts ./
COPY jaknaeso-core ./jaknaeso-core
COPY jaknaeso-server ./jaknaeso-server

RUN gradle --no-daemon clean :jaknaeso-server:bootJar -x test

# 실행 스테이지
FROM openjdk:21-jdk-slim
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
COPY --from=builder /build/jaknaeso-server/build/libs/jaknaeso-server.jar jaknaeso-server.jar
ENTRYPOINT ["java", "-jar", "/jaknaeso-server.jar"]