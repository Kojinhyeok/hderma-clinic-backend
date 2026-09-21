package com.hderma.clinic.domain.recruitment;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RecruitmentDto {

    @Getter @Setter
    public static class Request {
        private String trialCode;
        private String trialName;
        private String evalCategory;
        private String status;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer visitCount;
        private String testSite;
        private String participationGroup;
        private String requirements;
        private String productType;
        private String participationCost;
        private String recruitmentFieldIds;
        private String detailContent;
    }

    @Getter @Builder
    public static class Response {
        private Long id;
        private String trialCode;
        private String trialName;
        private String evalCategory;
        private String status;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer visitCount;
        private String testSite;
        private String participationGroup;
        private String requirements;
        private String productType;
        private String participationCost;
        private String recruitmentFieldIds;
        private String detailContent;
        private LocalDateTime createdAt;
    }
}