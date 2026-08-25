package com.hderma.clinic.domain.member;

import lombok.*;
import java.time.LocalDateTime;

public class MemberDto {

    @Getter @Setter
    public static class RegisterRequest {
        private String username;
        private String password;
        private String name;
        private String email;
        private String phone;
        private Boolean privacyAgreed;
    }

    @Getter @Setter
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Getter @Builder
    public static class Response {
        private Long id;
        private String username;
        private String name;
        private String email;
        private String phone;
        private String role;
        private String status;
        private LocalDateTime createdAt;
    }

    @Getter @Setter
    public static class RoleUpdateRequest {
        private String role; // MEMBER / PROFESSOR / ADMIN — 관리자 화면 전용
    }
}