package com.hderma.clinic.domain.irb;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IrbSurveyQuestionRepository extends JpaRepository<IrbSurveyQuestion, Long> {
    List<IrbSurveyQuestion> findByIsActiveOrderByQuestionOrderAsc(Integer isActive);
    List<IrbSurveyQuestion> findByQuestionCategoryAndIsActiveOrderByQuestionOrderAsc(String questionCategory, Integer isActive);
}