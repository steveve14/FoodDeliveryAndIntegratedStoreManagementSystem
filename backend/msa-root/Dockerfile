# ─── Stage 1: Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /workspace

# Gradle 캐시 최적화: 의존성 파일 먼저 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle/ gradle/
COPY config/ config/

# 각 서비스 build.gradle 복사
COPY service-auth/build.gradle service-auth/build.gradle
COPY service-user/build.gradle service-user/build.gradle
COPY service-store/build.gradle service-store/build.gradle
COPY service-order/build.gradle service-order/build.gradle
COPY service-delivery/build.gradle service-delivery/build.gradle
COPY service-event/build.gradle service-event/build.gradle
COPY service-gateway/build.gradle service-gateway/build.gradle
COPY service-discovery/build.gradle service-discovery/build.gradle

# 의존성 다운로드 (캐시 레이어)
RUN ./gradlew dependencies --no-daemon --quiet || true

# 전체 소스 복사 후 빌드
COPY . .

ARG SERVICE_NAME
RUN ./gradlew :${SERVICE_NAME}:bootJar --no-daemon -x test -x checkstyleMain -x spotlessCheck

# ─── Stage 2: Runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

ARG SERVICE_NAME
COPY --from=builder /workspace/${SERVICE_NAME}/build/libs/*.jar app.jar

# JVM 최적화 옵션 (컨테이너 내 메모리 인식)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
