package com.hderma.clinic.domain.irb;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "irb_survey_answer")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IrbSurveyAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "irb_test_id")
    private Long irbTestId;

    @Column(name = "survey_template_id")
    private Long surveyTemplateId;

    @Convert(converter = IrbJsonMapConverter.class)
    @Column(name = "answer_list", columnDefinition = "json")
    private Map<String, Object> answerList; // {"q1": true, "q2": "텍스트"}

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}