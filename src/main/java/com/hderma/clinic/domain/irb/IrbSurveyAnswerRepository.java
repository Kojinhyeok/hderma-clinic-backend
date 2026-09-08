package com.hderma.clinic.domain.irb;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IrbSurveyAnswerRepository extends JpaRepository<IrbSurveyAnswer, Long> {
    Optional<IrbSurveyAnswer> findByIrbTestIdAndMemberId(Long irbTestId, Long memberId);
    List<IrbSurveyAnswer> findByIrbTestId(Long irbTestId);
}