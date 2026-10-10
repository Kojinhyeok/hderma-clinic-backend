package com.hderma.clinic.domain.recruitment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 내 신청내역 화면용 응답 (신청 정보 + 시험 정보) */
@Getter @Builder
public class MyApplicationDto {
    private Long id;
    private Long recruitmentId;
    private String trialName;
    private String trialCode;
    private LocalDate preferredDate;
    private String preferredTimeSlot;
    private String inquiry;
    private String status;      // APPLIED / SELECTED / REJECTED / CANCELLED
    private LocalDateTime createdAt;
}