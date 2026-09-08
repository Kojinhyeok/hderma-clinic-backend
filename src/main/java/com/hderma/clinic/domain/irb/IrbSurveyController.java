package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/irb-surveys")
@RequiredArgsConstructor
public class IrbSurveyController {

    private final IrbSurveyService surveyService;

    @PostMapping
    public ResponseEntity<String> createSurvey(@RequestBody IrbDto.SurveyRequest req) {
        surveyService.survey(req);
        return ResponseEntity.ok("심사가 성공적으로 등록되었습니다.");
    }

    @GetMapping
    public ResponseEntity<List<IrbDto.SurveyResponse>> getAllSurveys() {
        return ResponseEntity.ok(surveyService.findAll());
    }

    @PutMapping("/{testId}")
    public ResponseEntity<String> updateSurvey(@PathVariable Long testId, @RequestParam Long memberId, @RequestBody IrbDto.SurveyRequest req) {
        surveyService.surveyUpdate(testId, memberId, req);
        return ResponseEntity.ok("심사 내용이 수정되었습니다.");
    }

    @DeleteMapping("/{testId}")
    public ResponseEntity<String> deleteSurvey(@PathVariable Long testId, @RequestParam Long memberId) {
        surveyService.delete(testId, memberId);
        return ResponseEntity.ok("심사 데이터가 삭제되었습니다.");
    }
}