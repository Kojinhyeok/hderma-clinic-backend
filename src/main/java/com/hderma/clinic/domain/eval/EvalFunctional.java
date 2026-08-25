package com.hderma.clinic.domain.eval;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eval_functional")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class EvalFunctional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    private String testPeriod;
    private String evalItems;
    private String subjectCount;

    @Builder.Default
    private Integer sortOrder = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}