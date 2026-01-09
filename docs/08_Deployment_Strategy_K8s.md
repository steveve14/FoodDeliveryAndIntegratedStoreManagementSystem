# 08. 배포 전략 및 Kubernetes 구성 (Deployment & K8s)

## 1. 컨테이너 빌드 전략 (Docker)
Java 17과 Spring Boot Jar 파일을 기반으로 경량 이미지를 생성합니다.

### Dockerfile 예시
```dockerfile
# Build Stage (Gradle 빌드)
FROM gradle:7.6-jdk17-alpine AS builder
WORKDIR /build
COPY . .
RUN ./gradlew build -x test --parallel

# Run Stage (실행)
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /build/build/libs/*.jar app.jar

# Timezone 설정 (KST)
ENV TZ=Asia/Seoul

# 실행
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "app.jar"]
```

## 2. Kubernetes 리소스 구조
각 마이크로서비스는 `Deployment`와 `Service` 리소스를 가집니다.

### deployment.yaml (예시)
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: store-service
  namespace: food-delivery
spec:
  replicas: 2 # 가용성을 위해 2개 파드 실행
  selector:
    matchLabels:
      app: store-service
  template:
    metadata:
      labels:
        app: store-service
    spec:
      containers:
        - name: store-service
          image: my-registry/store-service:latest
          ports:
            - containerPort: 8080
          readinessProbe: # 트래픽 받을 준비 됐는지 확인
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 20
          livenessProbe: # 살아있는지 확인
            httpGet:
              path: /actuator/health/liveness
              port: 8080
```

## 3. 외부 노출 전략 (Ingress)
외부(인터넷)에서의 접근은 `Nginx Ingress Controller`를 통해 `API Gateway`로만 허용합니다.
*   `api.mydomain.com` -> `API Gateway Service`
*   나머지 마이크로서비스(`Store`, `Order` 등)는 `ClusterIP` 타입으로 설정하여 외부 직접 접근 차단.
