package com.hderma.clinic.domain.recruitment;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recruitment")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Recruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trialCode;
    private String trialName;

    @Column(length = 30)
    private String evalCategory; // EFFICACY / FUNCTIONAL / HEALTH_FOOD / SAFETY

    @Builder.Default
    @Column(length = 20)
    private String status = "OPEN"; // URGENT / OPEN / CLOSED / PERMANENTLY_OPEN

    private LocalDate startDate;
    private LocalDate endDate;
    private Integer visitCount;
    private String testSite;

    private String participationGroup;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    private String productType;
    private String participationCost;
    private String recruitmentFieldIds; // 쉼표 구분

    // 홈페이지 노출 ON/OFF (OFF면 메인/참여신청 목록에서 숨김, 관리자 화면에는 계속 표시)
    // 기존 공고는 컬럼이 새로 생길 때 모두 ON(1)으로 시작
    @Builder.Default
    @Column(nullable = false, columnDefinition = "TINYINT(1) NOT NULL DEFAULT 1")
    private Boolean visible = true;

    @Column(columnDefinition = "LONGTEXT")
    private String detailContent;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}