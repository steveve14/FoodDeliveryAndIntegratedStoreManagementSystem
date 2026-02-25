package com.example.auth.service;
// AuthService.java
import com.example.auth.dto.TokenResponse;
import com.example.auth.grpc.client.UserGrpcClient;
import com.example.userservice.grpc.AuthenticateResponse;
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
 * 역할:
 * - 소셜 로그인(예: Google) 토큰 검증과 내부 로그인 로직(예: 이메일/비밀번호)을 담당하는 서비스 레이어입니다.
 * - 내부 MSA 통신은 gRPC를 사용합니다.
 */
@Service
@RequiredArgsConstructor
public class AuthService implements AuthenticationService {

    // application.yml에 설정된 Google OAuth 클라이언트 ID를 주입받아 검증 대상 audience로 사용합니다.
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    // JWT 발급용 유틸리티 (외부 주입)
    private final JwtProvider jwtProvider;
    private final UserGrpcClient userGrpcClient; // gRPC 클라이언트로 변경

    /**
     * 구글 ID 토큰을 검증하고, 검증된 정보를 바탕으로 내부 로직으로 로그인/회원등록을 수행한 뒤
     * 자체 JWT(access/refresh)를 발급합니다.
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
     * 이메일/비밀번호 로그인 - gRPC를 통해 service-user와 통신
     */
    @Transactional
    public TokenResponse login(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("이메일과 비밀번호를 입력해주세요.");
        }

        // gRPC를 통해 service-user의 authenticate 호출
        AuthenticateResponse response = userGrpcClient.authenticate(email, password);

        if (!response.getSuccess()) {
            throw new IllegalArgumentException(
                response.getErrorMessage().isEmpty() 
                    ? "이메일 또는 비밀번호가 올바르지 않습니다." 
                    : response.getErrorMessage()
            );
        }

        Long userId = response.getUserId();
        String role = response.getRoles();

        String accessToken = jwtProvider.createAccessToken(userId, role);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        return new TokenResponse(accessToken, refreshToken);
    }
}