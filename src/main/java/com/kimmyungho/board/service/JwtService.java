package com.kimmyungho.board.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private static final SecretKey key = Jwts.SIG.HS256.key().build();

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    public String getUsername(String accessToken) {
        return getSubject(accessToken);
    }

    private String generateToken(String subject) {
        var now = new Date();
        var exp = new Date(now.getTime() + (100 * 60 * 60 * 3)); // 3시간 이후 만료

        return Jwts.builder().subject(subject).signWith(key)
                .issuedAt(now) // 토큰 생성 시간
                .expiration(exp) // 토큰 만료 시간
                .compact();
    }

    private String getSubject(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseEncryptedClaims(token)
                    .getPayload()
                    .getSubject();
            // 토큰 검증 코드 공식 홈페이지 참조
        } catch (JwtException e) {
            logger.error("JwtException", e);
            throw  e;
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
}