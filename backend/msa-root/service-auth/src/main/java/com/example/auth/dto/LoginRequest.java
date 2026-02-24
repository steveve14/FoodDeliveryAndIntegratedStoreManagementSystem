package com.example.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * LoginRequest
 *
 * 역할:
 * - 이메일/비밀번호 로그인 요청에서 클라이언트가 전송하는 데이터 구조를 표현하는 DTO입니다.
 * - 컨트롤러에서 @RequestBody로 바인딩되어 AuthService로 전달됩니다.
 */
@Getter
@NoArgsConstructor
public class LoginRequest {
    // 사용자의 이메일
    private String email;
    // 사용자의 비밀번호(평문). 실제 서비스에서는 반드시 HTTPS를 사용하고 서버에서는 해시 비교를 해야 합니다.
    private String password;
}
