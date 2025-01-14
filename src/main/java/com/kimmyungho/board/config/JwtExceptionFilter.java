package com.kimmyungho.board.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        }
        catch(JwtException exception) {
        // TODO: JWT 관련 커스텀 에러메시지 생성에 RESPONSE 로 내려주기
            response.setContentType(MediaType.APPLICATION_JSON_VALUE); // JSON
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setCharacterEncoding("UTF-8");

            var errorMsg = new HashMap<String, Object>();
            errorMsg.put("status", HttpStatus.UNAUTHORIZED);
            errorMsg.put("message" , exception.getMessage());

            ObjectMapper objectMapper = new ObjectMapper();
            String responseJson = objectMapper.writeValueAsString(errorMsg);
            response.getWriter().write(responseJson);
        }
    }
}
