package com.hderma.clinic.domain.inquiry;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TrialRequestDto {

    @Getter @Setter
    public static class ItemInput {
        private String category; // 화면에서 보여주는 카테고리명 (예: "화장품 효능평가")
        private String name;     // 선택한 항목명 (예: "주름")
    }

    @Getter @Setter
    public static class Request {
        // 허니팟 — 값이 채워져 있으면 봇으로 간주하고 서버에서 조용히 무시
        private String website;

        private String companyName;
        private String businessRegNo;
        private String managerName;
        private String managerTitle;
        private String contact;
        private String email;
        private String productType;
        private LocalDate desiredStartDate;
        private LocalDate desiredReportDate;
        private String consultType;
        private String content;
        private String password;
        private Boolean privacyAgreed;
        private List<ItemInput> items;
    }

    @Getter @Builder
    public static class ItemResponse {
        private String category;
        private String name;
    }

    @Getter @Builder
    public static class Response {
        private Long id;
        private String companyName;
        private String managerName;
        private String contact;
        private String email;
        private String productType;
        private LocalDate desiredStartDate;
        private LocalDate desiredReportDate;
        private String consultType;
        private String content;
        private String status;
        private LocalDateTime createdAt;
        private List<ItemResponse> items;
    }

    @Getter @Setter
    public static class StatusUpdateRequest {
        private String status; // RECEIVED / IN_PROGRESS / COMPLETED
    }
}