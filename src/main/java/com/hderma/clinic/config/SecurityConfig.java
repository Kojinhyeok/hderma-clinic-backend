package com.hderma.clinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()   // 지금은 전부 오픈 — 로그인 로직 만들 때 재설정
            )
            .csrf(csrf -> csrf.disable()); // 폼 붙이기 전까지 임시로 꺼둠

        return http.build();
    }
}