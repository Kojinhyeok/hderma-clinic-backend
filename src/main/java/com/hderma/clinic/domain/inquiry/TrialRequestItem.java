package com.hderma.clinic.domain.inquiry;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trial_request_item")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TrialRequestItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trial_request_id")
    private Long trialRequestId;

    @Column(length = 30)
    private String evalSource; // EFFICACY_ITEM / FUNCTIONAL / HEALTH_FOOD / SAFETY / INVITRO

    private Long evalSourceId;
    private String itemNameSnapshot;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}