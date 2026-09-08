package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IrbSurveyTemplateService {

    private final IrbSurveyTemplateRepository templateRepository;

    @Transactional
    public IrbDto.SurveyTemplateResponse createTemplate(IrbDto.SurveyTemplateRequest req) {
        IrbSurveyTemplate entity = IrbSurveyTemplate.builder()
                .irbTestId(req.getIrbTestId())
                .surveyName(req.getSurveyName())
                .questionList(req.getQuestionList())
                .build();
        return toDto(templateRepository.save(entity));
    }

    // 특정 IRB 시험 전용 템플릿이 없으면 공통(irbTestId=null) 템플릿으로 폴백
    public IrbDto.SurveyTemplateResponse getTemplate(Long irbTestId, String surveyName) {
        return templateRepository.findByIrbTestIdAndSurveyName(irbTestId, surveyName)
                .or(() -> templateRepository.findByIrbTestIdAndSurveyName(null, surveyName))
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("해당 IRB 심사 템플릿을 찾을 수 없습니다."));
    }

    public List<IrbDto.SurveyTemplateResponse> getTemplatesAll() {
        return templateRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public IrbDto.SurveyTemplateResponse updateTemplate(Long id, IrbDto.SurveyTemplateRequest req) {
        IrbSurveyTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 템플릿이 존재하지 않습니다."));
        template.setSurveyName(req.getSurveyName());
        template.setQuestionList(req.getQuestionList());
        return toDto(template);
    }

    @Transactional
    public void delete(Long id) {
        IrbSurveyTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 템플릿이 존재하지 않습니다."));
        templateRepository.deleteById(template.getId());
    }

    private IrbDto.SurveyTemplateResponse toDto(IrbSurveyTemplate t) {
        return IrbDto.SurveyTemplateResponse.builder()
                .id(t.getId()).irbTestId(t.getIrbTestId()).surveyName(t.getSurveyName())
                .questionList(t.getQuestionList()).createdAt(t.getCreatedAt())
                .build();
    }
}