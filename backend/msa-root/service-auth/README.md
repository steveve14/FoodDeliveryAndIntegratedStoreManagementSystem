# service-auth

`service-auth`는 인증 관련 기능을 담당하는 마이크로서비스 템플릿입니다. 이 서비스는 소셜 로그인 검증(예: Google ID Token 검증)과 자체 JWT 발급을 위한 기초 로직을 포함합니다.

주요 엔드포인트

- POST /api/auth/google  : Google ID Token을 이용한 소셜 로그인 (body: JSON {"idToken":"..."})
- POST /api/auth/social  : provider, token 파라미터로 소셜 로그인 통합 엔드포인트
- POST /api/auth/login   : 이메일/비밀번호 로그인 (body: JSON {"email":"..","password":".."})

구성(주요 application.yml 프로퍼티)

- `token.secret` : JWT 서명에 사용할 시크릿(기본값은 안전하지 않음 — 운영 환경에서 반드시 설정)
- `token.expiration_time` : 액세스 토큰 유효기간(밀리초, 기본: 3600000 = 1시간)
- `token.refresh_time` : 리프레시 토큰 유효기간(밀리초, 기본: 1209600000 = 14일)
- `spring.security.oauth2.client.registration.google.client-id` : Google 로그인 검증용 클라이언트 ID

운영 권장 사항 (JWT 시크릿)

- HS256(대칭 서명)을 사용하므로 시크릿은 최소 256비트(32바이트) 이상이어야 합니다.
- 안전한 base64 인코딩된 32바이트 값을 사용하는 예:

  - OpenSSL에서 생성:

    ```bat
    openssl rand -base64 32
    ```

  - Java에서 임의 시크릿 생성(간단 예시):

    ```java
    byte[] bytes = new byte[32];
    new java.security.SecureRandom().nextBytes(bytes);
    String b64 = java.util.Base64.getEncoder().encodeToString(bytes);
    ```

- 생성한 값을 `application.yml`의 `token.secret`에 넣거나, 환경 변수로 주입하여 관리하세요. (예: CI/CD에서 비밀 관리, HashiCorp Vault, 클라우드 시크릿 매니저 권장)

실행 방법

- 전체 프로젝트 루트에서 Gradle wrapper를 이용:

  ```bat
  cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
  .\gradlew.bat :service-auth:bootRun
  ```

- 빠르게 빌드만 하려면:

  ```bat
  .\gradlew.bat :service-auth:clean :service-auth:build -x test
  ```

테스트 및 개발 팁

- Google 소셜 로그인을 로컬에서 테스트하려면 `spring.security.oauth2.client.registration.google.client-id` 값을 application.yml 또는 환경변수로 설정하세요.
- `JwtProvider`는 현재 HS256을 사용하며, 운영 환경에서는 키 길이 체크 및 비밀 저장소 사용을 권장합니다.

Spring REST Docs 생성

```bat
cd /d C:\Users\User\WorkStation\FoodDeliveryAndIntegratedStoreManagementSystem\backend\msa-root
.\gradlew.bat :service-auth:test :service-auth:asciidoctor
```

- 스니펫 출력 경로: `service-auth\build\generated-snippets`
- Asciidoc HTML 출력 경로(기본): `service-auth\build\docs\asciidoc`

디버깅

- 애플리케이션 실행 시 `PostConstruct` 초기화에서 시크릿 관련 예외가 발생하면 `token.secret`이 비어있거나 길이가 부족한 경우가 대부분입니다. 예외 메시지를 확인하고 안전한 시크릿을 설정하세요.

생성일: 2026-02-24
