package com.hderma.clinic.domain.recruitment;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrialApplicationDto {

    @Getter @Setter
    public static class Request {
        private Long recruitmentId;
        private Long memberId; // 로그인한 회원 ID — 컨트롤러에서 세션 붙이기 전까지는 프론트에서 임시로 넘김
        private String applicantName;
        private String applicantContact;
        private LocalDate applicantBirth;
    }

    @Getter @Builder
    public static class Response {
        private Long id;
        private Long recruitmentId;
        private Long memberId;
        private String applicantName;
        private String applicantContact;
        private LocalDate applicantBirth;
        private String status;
        private LocalDateTime createdAt;
    }

    @Getter @Setter
    public static class StatusUpdateRequest {
        private String status; // APPLIED / SELECTED / REJECTED / CANCELLED
    }
}