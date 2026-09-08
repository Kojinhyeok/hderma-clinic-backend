package com.hderma.clinic.domain.irb;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "irb_survey_question")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IrbSurveyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_category", length = 30)
    private String questionCategory; // DOCUMENTS, ETHICS, SCIENTIFICS, AGREEMENTS

    @Column(name = "question_type", length = 20)
    private String questionType; // BOOLEAN, PREFERENCE, TEXT

    @Column(name = "question_text", columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "question_order", columnDefinition = "INT DEFAULT 0")
    private Integer questionOrder;

    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Integer isActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}