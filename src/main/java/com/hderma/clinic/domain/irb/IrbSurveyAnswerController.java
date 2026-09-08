package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/irb-answers")
@RequiredArgsConstructor
public class IrbSurveyAnswerController {

    private final IrbSurveyAnswerService answerService;

    @PostMapping
    public ResponseEntity<IrbDto.AnswerResponse> submitAnswer(@RequestBody IrbDto.AnswerRequest req) {
        return ResponseEntity.ok(answerService.submitAnswer(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IrbDto.AnswerResponse> update(@PathVariable Long id, @RequestBody IrbDto.AnswerRequest req) {
        return ResponseEntity.ok(answerService.submitAnswer(req));
    }

    @GetMapping("/{irbTestId}")
    public ResponseEntity<IrbDto.AnswerResponse> getMyAnswer(@PathVariable Long irbTestId, @RequestParam Long memberId) {
        return ResponseEntity.ok(answerService.getAnswer(irbTestId, memberId));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        answerService.delete(id);
    }
}