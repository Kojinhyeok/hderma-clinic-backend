package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IrbSurveyQuestionService {

    private final IrbSurveyQuestionRepository questionRepository;

    public List<IrbDto.SurveyQuestionResponse> getAll() {
        return questionRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<IrbDto.SurveyQuestionResponse> getQuestionsByCategory(String category) {
        return questionRepository.findByQuestionCategoryAndIsActiveOrderByQuestionOrderAsc(category, 1)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public IrbDto.SurveyQuestionResponse createQuestion(IrbDto.SurveyQuestionRequest req) {
        IrbSurveyQuestion entity = IrbSurveyQuestion.builder()
                .questionCategory(req.getQuestionCategory())
                .questionType(req.getQuestionType())
                .questionText(req.getQuestionText())
                .questionOrder(req.getQuestionOrder())
                .isActive(req.getIsActive() != null ? req.getIsActive() : 1)
                .build();
        return toDto(questionRepository.save(entity));
    }

    @Transactional
    public IrbDto.SurveyQuestionResponse updateQuestion(Long id, IrbDto.SurveyQuestionRequest req) {
        IrbSurveyQuestion entity = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문항을 찾을 수 없습니다. id=" + id));
        entity.setQuestionText(req.getQuestionText());
        entity.setQuestionCategory(req.getQuestionCategory());
        entity.setQuestionType(req.getQuestionType());
        entity.setQuestionOrder(req.getQuestionOrder());
        entity.setIsActive(req.getIsActive());
        return toDto(entity);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        questionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("문항을 찾을 수 없습니다."));
        questionRepository.deleteById(id);
    }

    private IrbDto.SurveyQuestionResponse toDto(IrbSurveyQuestion q) {
        return IrbDto.SurveyQuestionResponse.builder()
                .id(q.getId()).questionCategory(q.getQuestionCategory()).questionType(q.getQuestionType())
                .questionText(q.getQuestionText()).questionOrder(q.getQuestionOrder()).isActive(q.getIsActive())
                .build();
    }
}