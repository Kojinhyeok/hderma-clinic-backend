package com.hderma.clinic.domain.irb;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IrbSurveyResultRepository extends JpaRepository<IrbSurveyResult, Long> {
    Optional<IrbSurveyResult> findByIrbTestIdAndMemberId(Long irbTestId, Long memberId);
    List<IrbSurveyResult> findAllByIrbTestId(Long irbTestId);
}