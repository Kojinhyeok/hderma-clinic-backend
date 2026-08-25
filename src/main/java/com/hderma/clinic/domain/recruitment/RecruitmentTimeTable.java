package com.hderma.clinic.domain.recruitment;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recruitment_time_table")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitmentTimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recruitment_id")
    private Long recruitmentId;

    private LocalDate visitDate;
    private String startTime;
    private String endTime;
    private Integer capacity;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}