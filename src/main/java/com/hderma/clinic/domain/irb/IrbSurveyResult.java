package com.hderma.clinic.domain.irb;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "irb_survey_result")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IrbSurveyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private Long irbTestId;
    private Long surveyTemplateId;
    private String reviewResult; // APPROVED, REJECTED
    private String reviewText;

    @CreationTimestamp
    private LocalDateTime createdAt;
}