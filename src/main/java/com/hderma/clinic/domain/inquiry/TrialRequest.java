package com.hderma.clinic.domain.inquiry;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trial_request")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TrialRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String businessRegNo;
    private String managerName;
    private String managerTitle;
    private String contact;
    private String email;
    private String productType;
    private LocalDate desiredStartDate;
    private LocalDate desiredReportDate;

    @Column(length = 20)
    private String consultType; // PHONE / EMAIL

    @Column(columnDefinition = "TEXT")
    private String content;

    private String password;

    @Builder.Default
    private Boolean privacyAgreed = false;

    @Builder.Default
    @Column(length = 20)
    private String status = "RECEIVED"; // RECEIVED / IN_PROGRESS / COMPLETED

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}