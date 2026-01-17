# Kubernetes Actions Runner Controller (ARC) 설치 가이드

이 문서는 Kubernetes 클러스터에 Self-Hosted GitHub Actions Runner를 구축하는 절차를 정리한 가이드입니다.

## 1. 사전 요구 사항
* Kubernetes Cluster
* Helm (Package Manager)
* GitHub Personal Access Token (PAT)
    * **Scope:** `repo` (전체), `workflow`, `admin:org` (조직 레벨 연동 시)

---

## 2. Cert-Manager 설치
ARC 작동에 필요한 인증서 관리 도구를 설치합니다.

```bash
kubectl apply -f [https://github.com/cert-manager/cert-manager/releases/download/v1.13.1/cert-manager.yaml](https://github.com/cert-manager/cert-manager/releases/download/v1.13.1/cert-manager.yaml)

# 설치 확인 (파드들이 Running 상태가 될 때까지 대기)
kubectl get pods -n cert-manager

```

---

## 3. Controller 설치 (Helm)

Actions Runner Controller를 `actions-runner-system` 네임스페이스에 설치합니다.

```bash
# Helm 리포지토리 추가
helm repo add actions-runner-controller [https://actions-runner-controller.github.io/actions-runner-controller](https://actions-runner-controller.github.io/actions-runner-controller)
helm repo update

# 컨트롤러 설치
helm upgrade --install --namespace actions-runner-system --create-namespace \
  controller actions-runner-controller/actions-runner-controller

```

---

## 4. GitHub 인증 토큰 등록

GitHub와 통신하기 위한 PAT를 Secret으로 생성합니다.
(`YOUR_GITHUB_TOKEN` 부분을 실제 토큰 값으로 변경하세요)

```bash
kubectl create secret generic controller-manager \
    -n actions-runner-system \
    --from-literal=github_token=YOUR_GITHUB_TOKEN

```

---

## 5. Runner 배포

실제 빌드를 수행할 Runner 파드를 생성합니다.

**1) `runner.yaml` 파일 작성**

```yaml
apiVersion: actions.summerwind.dev/v1alpha1
kind: RunnerDeployment
metadata:
  name: my-runner
  namespace: actions-runner-system
spec:
  replicas: 1
  template:
    spec:
      # [옵션 1] 특정 리포지토리 전용
      repository: USER_NAME/REPO_NAME
      
      # [옵션 2] 조직(Organization) 전용 (위 repository 주석 처리 후 사용)
      # organization: ORG_NAME
      
      labels:
        - self-hosted
        - linux
        - x64

```

```
cat <<EOF > my-runner.yaml
apiVersion: actions.summerwind.dev/v1alpha1
kind: RunnerDeployment
metadata:
  name: my-runner
  namespace: actions-runner-system
spec:
  replicas: 1
  template:
    spec:
      repository: steveve14/FoodDeliveryAndIntegratedStoreManagementSystem
      
      # [핵심] 자동 설정 끄기 (수동으로 넣을 것이므로)
      dockerEnabled: false
      
      # 러너 컨테이너 (1번)
      image: summerwind/actions-runner:latest
      env:
        - name: DOCKER_HOST
          value: tcp://localhost:2375
          
      # 도커 엔진 컨테이너 (2번)
      sidecarContainers:
        - name: docker
          image: docker:20.10-dind
          securityContext:
            privileged: true  # Rocky Linux에서는 이게 필수입니다!
          env:
            - name: DOCKER_TLS_CERTDIR
              value: ""
          volumeMounts:
            - name: docker-storage
              mountPath: /var/lib/docker
            - name: work
              mountPath: /runner/_work

      # 저장소 볼륨 설정
      volumes:
        - name: docker-storage
          emptyDir: {}
        - name: work
          emptyDir: {}
EOF
```

**2) 적용**

```bash
kubectl apply -f runner.yaml

```

---

## 6. 설치 확인 및 트러블슈팅

**상태 확인 명령어**

```bash
kubectl get pods -n actions-runner-system

```

**상태별 의미**

* **ContainerCreating:** 이미지 풀링 또는 네트워크 할당 중. (오래 걸리면 `kubectl describe`로 이벤트 확인)
* **Running (Ready 2/2):** 정상 실행 중.
* **로그 확인:** `kubectl logs -f <RUNNER_POD_NAME> -n actions-runner-system`
* `Listening for Jobs` 메시지가 보이면 연결 성공.
