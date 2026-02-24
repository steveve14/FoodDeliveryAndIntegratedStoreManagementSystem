package com.example.auth.service;
// AuthService.java
import com.example.auth.dto.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.example.auth.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * AuthService
 *
 * 역할:
 * - 소셜 로그인(예: Google) 토큰 검증과 내부 로그인 로직(예: 이메일/비밀번호)을 담당하는 서비스 레이어입니다.
 * - 실제 사용자 저장/조회(UserService)나 사용자 엔티티는 분리되어 있어야 하며, 이 클래스는 그 부분을 호출하도록 구현해야 합니다.
 * - 현재는 예시/샘플 코드로 일부 동작은 더미 값으로 처리되어 있습니다.
 */
@Service
@RequiredArgsConstructor
public class AuthService implements AuthenticationService {

    // application.yml에 설정된 Google OAuth 클라이언트 ID를 주입받아 검증 대상 audience로 사용합니다.
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    // JWT 발급용 유틸리티 (외부 주입)
    private final JwtProvider jwtProvider;

    /**
     * 구글 ID 토큰을 검증하고, 검증된 정보를 바탕으로 내부 로직으로 로그인/회원등록을 수행한 뒤
     * 자체 JWT(access/refresh)를 발급합니다.
     *
     * 이 메서드는 주요 단계:
     * 1) GoogleIdTokenVerifier로 토큰 유효성 검증
     * 2) payload에서 이메일, 이름, subject(google id) 추출
     * 3) 내부 사용자 저장/조회 (주석 처리된 부분)
     * 4) JwtProvider로 자체 토큰 발급 (현재는 더미 토큰 반환)
     */
    @Transactional
    public TokenResponse verifyGoogleTokenAndLogin(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            // 1. 구글 토큰 검증
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new IllegalArgumentException("유효하지 않은 구글 ID 토큰입니다.");
            }

            // 2. 유저 정보 추출
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String googleProviderId = payload.getSubject();

            // 3. 유저 정보 확인 및 저장 (MSA 환경이므로 service-user와 통신하거나 DB 조회)
            // User user = userService.findByEmailOrSave(email, name, googleProviderId);

            // 4. 서비스 자체 JWT 생성
            // String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole());
            // String refreshToken = jwtProvider.createRefreshToken(user.getId());

            // 현재는 예시로 더미 토큰을 반환합니다. 실제 구현 시 위 주석 처리된 부분을 활성화하세요.
            return new TokenResponse("dummy_access_token", "dummy_refresh_token");

        } catch (Exception e) {
            throw new RuntimeException("구글 로그인 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional
    public TokenResponse socialLogin(String provider, String token) {
        // 입력값 검증: provider와 token이 비어있으면 오류
        if (provider == null || token == null) {
            throw new IllegalArgumentException("provider와 token을 입력해주세요.");
        }

        switch (provider.toLowerCase()) {
            case "google":
                return verifyGoogleTokenAndLogin(token);
            // case "kakao":
            //     return verifyKakaoTokenAndLogin(token);
            default:
                throw new IllegalArgumentException("지원하지 않는 소셜 로그인 제공자입니다: " + provider);
        }
    }

    /**
     * 간단한 이메일/비밀번호 로그인 예시
     * - 실제로는 UserService로 사용자 조회 및 비밀번호(해시) 검증이 필요합니다.
     * - 여기서는 데모를 위해 하드코드된 값으로 검증합니다.
     */
    @Transactional
    public TokenResponse login(String email, String password) {
        // 필수 입력 체크
        if (email == null || password == null) {
            throw new IllegalArgumentException("이메일과 비밀번호를 입력해주세요.");
        }

        // 임시 검증 로직(프로덕션에서는 절대 사용하지 마세요)
        if (!email.equals("user@example.com") || !password.equals("password")) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 임시 userId와 role (실제 시스템에서는 DB의 사용자 ID/권한 정보를 사용)
        Long userId = 1L;
        String role = "USER";

        // JwtProvider를 통해 액세스/리프레시 토큰 생성
        String accessToken = jwtProvider.createAccessToken(userId, role);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        return new TokenResponse(accessToken, refreshToken);
    }
}