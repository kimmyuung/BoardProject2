package com.kimmyungho.board.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
}
