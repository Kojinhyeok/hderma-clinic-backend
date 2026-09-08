package com.hderma.clinic.domain.irb;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "irb_survey_template")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class IrbSurveyTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long irbTestId;
    private String surveyName; // BASIC, EXTRA

    @Convert(converter = IrbJsonConverter.class)
    @Column(name = "question_list", columnDefinition = "json")
    private List<IrbDto.QuestionItem> questionList;

    @CreationTimestamp
    private LocalDateTime createdAt;
}