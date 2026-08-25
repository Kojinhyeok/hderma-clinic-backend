package com.hderma.clinic.domain.recruitment;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trial_application")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TrialApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recruitment_id")
    private Long recruitmentId;

    @Column(name = "member_id")
    private Long memberId;

    private String applicantName;
    private String applicantContact;
    private LocalDate applicantBirth;

    @Builder.Default
    @Column(length = 20)
    private String status = "APPLIED"; // APPLIED / SELECTED / REJECTED / CANCELLED

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}