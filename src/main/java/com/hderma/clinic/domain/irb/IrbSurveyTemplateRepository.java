package com.hderma.clinic.domain.irb;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IrbSurveyTemplateRepository extends JpaRepository<IrbSurveyTemplate, Long> {
    List<IrbSurveyTemplate> findByIrbTestId(Long irbTestId);
    Optional<IrbSurveyTemplate> findByIrbTestIdAndSurveyName(Long irbTestId, String surveyName);
}