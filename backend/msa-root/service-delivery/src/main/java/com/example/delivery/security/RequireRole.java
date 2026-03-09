package com.example.delivery.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 메서드 또는 클래스에 필요한 역할을 지정합니다. Gateway가 전달하는 X-User-Role 헤더를 기준으로 검증됩니다. */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
  /** 허용할 역할 목록 */
  String[] value();
}
