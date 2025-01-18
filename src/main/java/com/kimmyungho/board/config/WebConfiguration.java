package com.kimmyungho.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class WebConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final JwtExceptionFilter jwtExceptionFilter;

    public WebConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, JwtExceptionFilter jwtExceptionFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtExceptionFilter = jwtExceptionFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000" , "http://127.0.0.1:3000" ));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST" , "PATCH" , "DELETE" ));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("api/v1/**", corsConfiguration);
        return source;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST,
                                "/api/*/users",
                                "/api/*/users/authenticate"
                        )
                        .permitAll()
                        .anyRequest().authenticated())
                // 사용자의 어느 요청에 대해서건 검증이 필요
                .sessionManagement(
                        (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 사용자 인증 세션 생성되지 않음
                .csrf(CsrfConfigurer::disable)
                // csrf 인증 제외
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, jwtAuthenticationFilter.getClass())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }



    // JWT
    // JSON 기반의 인증토큰
    // 인증 정보가 토큰에 담겨 있으므로, Statless 서버에 적합
    // HTTP Request Headers 에 Authorization: Bearer ACCESS_TOKEN 형태로 포삼시켜 사용
    // JWT의 페이로드는 BASE64 인코딩되어 있어 쉽게 디코딩하여 확인 가능
    // 페이로드에 너무 많은 데이터를 담거나, 민감 정버를 담지 말 것
    // 유효기간을 짧게 설정할 것
    // HEADER // PAYLOAD // VERIFY SIGNNATURE
}
