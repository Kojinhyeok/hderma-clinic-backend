package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IrbSurveyResultService {

    private final IrbSurveyResultRepository resultRepository;

    @Transactional
    public IrbDto.ResultResponse submitReviewResult(IrbDto.ResultRequest req) {
        IrbSurveyResult entity = resultRepository
                .findByIrbTestIdAndMemberId(req.getIrbTestId(), req.getMemberId())
                .map(existing -> {
                    existing.setReviewResult(req.getReviewResult());
                    existing.setReviewText(req.getReviewText());
                    existing.setSurveyTemplateId(req.getSurveyTemplateId());
                    return existing;
                })
                .orElseGet(() -> IrbSurveyResult.builder()
                        .memberId(req.getMemberId())
                        .irbTestId(req.getIrbTestId())
                        .surveyTemplateId(req.getSurveyTemplateId())
                        .reviewResult(req.getReviewResult())
                        .reviewText(req.getReviewText())
                        .build());

        return toDto(resultRepository.save(entity));
    }

    public List<IrbDto.ResultResponse> getAllResult() {
        return resultRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<IrbDto.ResultResponse> getResultByTestId(Long irbTestId) {
        return resultRepository.findAllByIrbTestId(irbTestId).stream().map(this::toDto).toList();
    }

    public IrbDto.ResultResponse getMyResult(Long irbTestId, Long memberId) {
        return resultRepository.findByIrbTestIdAndMemberId(irbTestId, memberId)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        resultRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("삭제할 심사결과가 없습니다."));
        resultRepository.deleteById(id);
    }

    private IrbDto.ResultResponse toDto(IrbSurveyResult r) {
        return IrbDto.ResultResponse.builder()
                .id(r.getId()).memberId(r.getMemberId()).irbTestId(r.getIrbTestId())
                .surveyTemplateId(r.getSurveyTemplateId()).reviewResult(r.getReviewResult())
                .reviewText(r.getReviewText()).createdAt(r.getCreatedAt())
                .build();
    }
}