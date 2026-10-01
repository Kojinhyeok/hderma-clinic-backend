package com.hderma.clinic.domain.recruitment;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "recruitment_slot")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recruitmentId;

    private LocalDate date;

    @Column(length = 10)
    private String time; // "09:00" 형식

    @Builder.Default
    private Integer capacity = 1;
}