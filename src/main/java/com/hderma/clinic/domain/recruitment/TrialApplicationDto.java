package com.hderma.clinic.domain.recruitment;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrialApplicationDto {

    @Getter @Setter
    public static class Request {
        private Long recruitmentId;
        private Long memberId; // 서버(컨트롤러)에서 로그인 세션값으로 채움 — 클라이언트가 보내도 무시됨
        private String applicantName;    // 서버에서 회원정보로 채움 — 클라이언트가 보내도 무시됨
        private String applicantContact; // 서버에서 회원정보로 채움 — 클라이언트가 보내도 무시됨
        private LocalDate applicantBirth; // 서버에서 회원정보로 채움(없으면 null) — 클라이언트가 보내도 무시됨
        private LocalDate preferredDate;
        private String preferredTimeSlot;
        private String inquiry;
    }

    @Getter @Builder
    public static class Response {
        private Long id;
        private Long recruitmentId;
        private Long memberId;
        private String applicantName;
        private String applicantContact;
        private LocalDate applicantBirth;
        private LocalDate preferredDate;
        private String preferredTimeSlot;
        private String inquiry;
        private String status;
        private LocalDateTime createdAt;
    }

    @Getter @Setter
    public static class StatusUpdateRequest {
        private String status; // APPLIED / SELECTED / REJECTED / CANCELLED
    }
}