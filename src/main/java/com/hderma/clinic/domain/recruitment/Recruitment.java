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
    private String status = "OPEN"; // OPEN / CLOSED / PERMANENTLY_OPEN

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;

    private String participationGroup;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    private Integer participationNumber;
    private String participationCost;
    private String recruitmentFieldIds; // 쉼표 구분

    @Column(columnDefinition = "LONGTEXT")
    private String detailContent;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}