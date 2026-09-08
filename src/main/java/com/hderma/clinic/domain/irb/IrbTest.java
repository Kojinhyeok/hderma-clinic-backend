package com.hderma.clinic.domain.irb;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "irb_test")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class IrbTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "is_temp", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Integer isTemp; // 0: 운영, 1: 임시저장

    @Column(name = "irb_test_id")
    private Long irbTestId; // 원글 고유 ID

    @Column(name = "irb_test_id_ref")
    private Long irbTestIdRef; // 답글인 경우 상위글의 irb_test_id

    @Column(name = "depth", columnDefinition = "INT DEFAULT 0")
    private Integer depth;

    @Column(name = "irb_code", length = 50)
    private String irbCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    @Builder.Default
    private IrbStatus status = IrbStatus.IN_REVIEW;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}