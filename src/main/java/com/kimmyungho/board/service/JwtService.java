package com.kimmyungho.board.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}") // application.properties에서 주입
    private String secret;

    @Value("${jwt.expiration}") // application.properties에서 주입
    private long expiration; // 밀리초 단위

    // 시크릿 키 생성
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 액세스 토큰 생성
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // 필요 시 추가 클레임 추가 (예: 역할)
        return generateToken(claims, userDetails.getUsername());
    }

    // 토큰에서 사용자 이름 추출
    public String getUsername(String token) {
        return getSubject(token);
    }

    // 토큰 생성
    private String generateToken(Map<String, Object> claims, String subject) {
        var now = new Date();
        var exp = new Date(now.getTime() + expiration); // 만료 시간 설정

        return Jwts.builder()
                .claims(claims) // 추가 클레임
                .subject(subject) // 주제 (일반적으로 사용자 이름)
                .issuedAt(now) // 토큰 발행 시간
                .expiration(exp) // 토큰 만료 시간
                .signWith(getSigningKey()) // 서명
                .compact();
    }

    // 토큰에서 주제(subject) 추출
    private String getSubject(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey()) // 서명 검증
                    .build()
                    .parseSignedClaims(token) // 토큰 파싱
                    .getPayload()
                    .getSubject(); // 주제 반환
        } catch (JwtException e) {
            logger.error("JWT 검증 실패: {}", e.getMessage());
            throw new JwtValidationException("유효하지 않은 JWT 토큰", e);
        }
    }

    // 커스텀 예외 클래스
    public static class JwtValidationException extends RuntimeException {
        public JwtValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
//private String getSubject(String token) {
//    try {
//        // JWT 토큰을 파싱하고 검증
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(key) // 서명 키 설정
//                .build()
//                .parseClaimsJws(token) // 토큰 파싱 및 검증
//                .getBody();
//
//        // subject 클레임 반환
//        return claims.getSubject();
//    } catch (ExpiredJwtException e) {
//        // 토큰 만료 시
//        logger.error("JWT Token is expired: {}", e.getMessage());
//        throw new JwtAuthenticationException("Token has expired", e);
//    } catch (MalformedJwtException e) {
//        // 잘못된 형식의 토큰
//        logger.error("Invalid JWT Token format: {}", e.getMessage());
//        throw new JwtAuthenticationException("Invalid token format", e);
//    } catch (SignatureException e) {
//        // 서명이 일치하지 않음
//        logger.error("Invalid JWT Token signature: {}", e.getMessage());
//        throw new JwtAuthenticationException("Invalid token signature", e);
//    } catch (JwtException e) {
//        // 기타 JWT 관련 예외
//        logger.error("JWT Token is invalid: {}", e.getMessage());
//        throw new JwtAuthenticationException("Invalid JWT Token", e);
//    }
//}
