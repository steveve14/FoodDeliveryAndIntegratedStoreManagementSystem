package com.example.auth.security;

// JWT 생성 및 서명에 사용하는 JJWT 라이브러리의 클래스들
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
// Spring에서 프로퍼티 주입(@Value)과 컴포넌트 스캐닝(@Component)을 사용
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Jakarta의 PostConstruct 애노테이션: 빈 초기화 후 init()을 호출하기 위해 사용
import jakarta.annotation.PostConstruct;
// 대칭 키(HMAC)용 SecretKey 타입
import javax.crypto.SecretKey;
// 토큰 발행 시점 및 만료 시점을 계산하기 위한 시간 API
import java.time.Instant;
// Base64 디코딩(환경에 따라 secret이 base64로 제공될 수 있음)
import java.util.Base64;
import java.util.Date;

/**
 * JwtProvider
 *
 * 역할:
 * - 애플리케이션에서 JWT(액세스 토큰, 리프레시 토큰)를 생성하고 서명하는 책임을 가진 Spring 컴포넌트입니다.
 * - 구성은 application.yml(또는 환경 변수)을 통해 주입되는 값을 사용합니다.
 *
 * 주의:
 * - 현재 구현은 HS256(HMAC-SHA256) 대칭 서명을 사용합니다. 서비스 간 검증 / 키 회전을 고려한다면
 *   비대칭(RS256)으로 전환하는 것을 권장합니다.
 */
@Component
public class JwtProvider {

    // 토큰 서명에 사용할 비밀(대칭 키). application.yml 또는 환경변수로 제공됩니다.
    // 기본값은 안전하지 않은 자리표시자입니다: 운영 환경에서는 반드시 안전한 랜덤 시크릿(최소 256비트)을 설정해야 합니다.
    @Value("${token.secret:changeit-please-use-a-secure-random-secret-with-min-256-bits}")
    private String tokenSecret;

    // 액세스 토큰의 유효기간(밀리초 단위). application.yml로 설정 가능.
    @Value("${token.expiration_time:3600000}") // 기본 1시간
    private long accessTokenValidityMs;

    // 리프레시 토큰의 유효기간(밀리초 단위). application.yml로 설정 가능.
    @Value("${token.refresh_time:1209600000}") // 기본 14일
    private long refreshTokenValidityMs;

    // 실제 서명에 사용할 SecretKey 객체. init()에서 tokenSecret으로부터 생성됩니다.
    private SecretKey key;

    /**
     * 빈 초기화 시 실행되는 메소드.
     *
     * 동작:
     * - 먼저 tokenSecret을 Base64로 디코딩 시도합니다. (비밀이 base64로 저장되어 있을 수 있음)
     * - 디코딩이 실패하면 원시 문자열의 바이트를 사용합니다.
     * - 최종적으로 Keys.hmacShaKeyFor(...)를 호출하여 SecretKey를 생성합니다.
     *
     * 주의/권고:
     * - 현재 코드는 tokenSecret.getBytes()를 사용하므로 플랫폼 기본 문자셋에 영향을 받을 수 있습니다.
     *   안정적으로 하려면 UTF-8을 명시적으로 사용해야 합니다 (예: tokenSecret.getBytes(StandardCharsets.UTF_8)).
     * - tokenSecret이 비어있거나 너무 짧으면 보안상 취약하므로 운영 환경에서는 반드시 256비트(32바이트) 이상의 비밀을 설정하세요.
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes;
        try {
            // 먼저 Base64 디코드 시도: 환경에서 base64로 전달되는 경우를 지원
            keyBytes = Base64.getDecoder().decode(tokenSecret);
        } catch (IllegalArgumentException ex) {
            // Base64가 아니면 원시 문자열에서 바이트를 추출하여 사용
            // (주의: 기본 문자셋에 따라 달라질 수 있음)
            keyBytes = tokenSecret.getBytes();
        }

        // Keys.hmacShaKeyFor는 주어진 바이트로부터 HMAC 키를 생성합니다.
        // 내부적으로 키 길이(최소 256비트) 등 조건을 확인하므로, 너무 짧으면 예외가 발생할 수 있습니다.
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 액세스 토큰 생성
     *
     * 파라미터:
     * - userId: 토큰의 subject(sub)으로 들어갈 사용자 식별자(필수)
     * - role: 사용자 권한 정보(예: "ROLE_USER")를 role 클레임으로 포함
     *
     * 토큰 내용:
     * - subject(sub): userId
     * - claim("role"): role 정보
     * - iat: 발행 시각
     * - exp: 만료 시각(현재 시각 + accessTokenValidityMs)
     * - 서명: HS256과 위에서 생성된 SecretKey로 서명
     *
     * 반환값: 서명된 JWT 문자열
     */
    public String createAccessToken(Long userId, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                // JWT 표준 클레임 subject에 사용자 ID를 문자열로 저장
                .setSubject(userId.toString())
                // 커스텀 클레임으로 권한(role) 정보를 추가
                .claim("role", role)
                // 발행 시간(iat)
                .setIssuedAt(Date.from(now))
                // 만료 시간(exp)
                .setExpiration(Date.from(now.plusMillis(accessTokenValidityMs)))
                // 키와 알고리즘으로 서명
                .signWith(key, SignatureAlgorithm.HS256)
                // compact()로 최종 토큰 문자열 생성
                .compact();
    }

    /**
     * 리프레시 토큰 생성
     *
     * 파라미터:
     * - userId: 토큰의 subject(sub)으로 들어갈 사용자 식별자(필수)
     *
     * 토큰 내용:
     * - subject(sub): userId
     * - claim("type"): "refresh" 로 리프레시 토큰임을 표시
     * - iat: 발행 시각
     * - exp: 만료 시각(현재 시각 + refreshTokenValidityMs)
     * - 서명: HS256과 SecretKey로 서명
     *
     * 반환값: 서명된 JWT 문자열
     *
     * 권고:
     * - 리프레시 토큰의 경우 서버 측에서 jti(토큰 식별자)를 발급해 DB에 저장하고 폐기(revoke) 처리를 하는
     *   패턴이 안전합니다(여기서는 jti를 추가/저장하지 않음).
     */
    public String createRefreshToken(Long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                // JWT 표준 클레임 subject에 사용자 ID를 문자열로 저장
                .setSubject(userId.toString())
                // 리프레시 토큰임을 명시하는 커스텀 클레임
                .claim("type", "refresh")
                // 발행 시간(iat)
                .setIssuedAt(Date.from(now))
                // 만료 시간(exp)
                .setExpiration(Date.from(now.plusMillis(refreshTokenValidityMs)))
                // 키와 알고리즘으로 서명
                .signWith(key, SignatureAlgorithm.HS256)
                // compact()로 최종 토큰 문자열 생성
                .compact();
    }
}