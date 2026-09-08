package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IrbSurveyAnswerService {

    private final IrbSurveyAnswerRepository answerRepository;

    @Transactional
    public IrbDto.AnswerResponse submitAnswer(IrbDto.AnswerRequest req) {
        IrbSurveyAnswer entity = answerRepository
                .findByIrbTestIdAndMemberId(req.getIrbTestId(), req.getMemberId())
                .map(existing -> {
                    existing.setAnswerList(req.getAnswerList());
                    existing.setSurveyTemplateId(req.getSurveyTemplateId());
                    return existing;
                })
                .orElseGet(() -> answerRepository.save(IrbSurveyAnswer.builder()
                        .memberId(req.getMemberId())
                        .irbTestId(req.getIrbTestId())
                        .surveyTemplateId(req.getSurveyTemplateId())
                        .answerList(req.getAnswerList())
                        .build()));

        return toDto(entity);
    }

    public IrbDto.AnswerResponse getAnswer(Long irbTestId, Long memberId) {
        return answerRepository.findByIrbTestIdAndMemberId(irbTestId, memberId)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("작성된 답변을 찾을 수 없습니다."));
    }

    public List<IrbDto.AnswerResponse> getAnswersByTestId(Long irbTestId) {
        return answerRepository.findByIrbTestId(irbTestId).stream().map(this::toDto).toList();
    }

    @Transactional
    public void delete(Long id) {
        answerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 답변입니다."));
        answerRepository.deleteById(id);
    }

    private IrbDto.AnswerResponse toDto(IrbSurveyAnswer a) {
        return IrbDto.AnswerResponse.builder()
                .id(a.getId()).memberId(a.getMemberId()).irbTestId(a.getIrbTestId())
                .surveyTemplateId(a.getSurveyTemplateId()).answerList(a.getAnswerList())
                .createdAt(a.getCreatedAt()).updatedAt(a.getUpdatedAt())
                .build();
    }
}