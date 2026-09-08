package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IrbSurveyService {

    private final IrbSurveyResultService resultService;
    private final IrbSurveyAnswerService answerService;
    private final IrbTestService testService;

    // 심사 작성 (답변 + 결과 동시 저장)
    public void survey(IrbDto.SurveyRequest req) {
        answerService.submitAnswer(req.getAnswer());
        resultService.submitReviewResult(req.getResult());
    }

    // 전체 심사 현황 조회 (원글 x 답변 단위로 조립)
    public List<IrbDto.SurveyResponse> findAll() {
        return testService.findAll().stream()
                .flatMap(test -> {
                    List<IrbDto.AnswerResponse> answers = answerService.getAnswersByTestId(test.getId());
                    return answers.stream().map(answer -> {
                        IrbDto.ResultResponse result = resultService.getMyResult(test.getId(), answer.getMemberId());
                        return IrbDto.SurveyResponse.builder()
                                .test(test).answer(answer).result(result)
                                .build();
                    });
                })
                .collect(Collectors.toList());
    }

    // 심사 수정
    public void surveyUpdate(Long testId, Long memberId, IrbDto.SurveyRequest req) {
        IrbDto.AnswerResponse foundAnswer = answerService.getAnswer(testId, memberId);
        IrbDto.AnswerRequest answerReq = IrbDto.AnswerRequest.builder()
                .memberId(memberId).irbTestId(testId)
                .surveyTemplateId(req.getAnswer().getSurveyTemplateId())
                .answerList(req.getAnswer().getAnswerList())
                .build();
        answerService.submitAnswer(answerReq);

        IrbDto.ResultRequest resultReq = IrbDto.ResultRequest.builder()
                .memberId(memberId).irbTestId(testId)
                .surveyTemplateId(req.getAnswer().getSurveyTemplateId())
                .reviewResult(req.getResult().getReviewResult())
                .reviewText(req.getResult().getReviewText())
                .build();
        resultService.submitReviewResult(resultReq);
    }

    // 심사 삭제
    public void delete(Long testId, Long memberId) {
        IrbDto.AnswerResponse foundAnswer = answerService.getAnswer(testId, memberId);
        answerService.delete(foundAnswer.getId());
        IrbDto.ResultResponse foundResult = resultService.getMyResult(testId, memberId);
        if (foundResult != null) resultService.delete(foundResult.getId());
    }
}