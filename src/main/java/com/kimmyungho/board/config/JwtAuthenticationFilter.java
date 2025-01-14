package com.kimmyungho.board.config;

import com.kimmyungho.board.exception.jwt.JwtTokenNotFoundException;
import com.kimmyungho.board.service.JwtService;
import com.kimmyungho.board.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.http.SecurityHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired private JwtService jwtService;
    @Autowired private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*
         TODO : JWT 검증
         Http Header : Authorization Bearer JWT_ACCESS_TOKEN 토큰값
        */
        String BEARER_PREFIX = "Bearer ";
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        var securityContext = SecurityContextHolder.getContext();

        /*
        JWT 토큰값 생성 실패
        if (ObjectUtils.isEmpty(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            throw new JwtTokenNotFoundException();
        }*/

        if (!ObjectUtils.isEmpty(authorization)
                && authorization.startsWith(BEARER_PREFIX)
                && securityContext.getAuthentication() == null) {
            var accessToken = authorization.substring(BEARER_PREFIX.length());
            var username = jwtService.getUsername(accessToken);
            var userDeatils = userService.loadUserByUsername(username);

            var authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDeatils,
                    null, // 패스워드 저장 안하므로 필요 없음
                    userDeatils.getAuthorities() // 권한 정보
            ); // 사용자 인증정보 토큰 저장 위해 사용


            // 인증 정보를 저장
            // 컨트롤러에서 사용 위해 필요
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);
        }
        filterChain.doFilter(request, response);
    }
}
