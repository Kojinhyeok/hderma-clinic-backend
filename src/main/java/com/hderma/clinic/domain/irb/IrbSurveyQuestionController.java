package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/irb-questions")
@RequiredArgsConstructor
public class IrbSurveyQuestionController {

    private final IrbSurveyQuestionService questionService;

    @GetMapping
    public ResponseEntity<List<IrbDto.SurveyQuestionResponse>> getAll() {
        return ResponseEntity.ok(questionService.getAll());
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<IrbDto.SurveyQuestionResponse>> getQuestions(@PathVariable String category) {
        return ResponseEntity.ok(questionService.getQuestionsByCategory(category));
    }

    @PostMapping
    public ResponseEntity<IrbDto.SurveyQuestionResponse> createQuestion(@RequestBody IrbDto.SurveyQuestionRequest req) {
        return ResponseEntity.ok(questionService.createQuestion(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IrbDto.SurveyQuestionResponse> updateQuestion(@PathVariable Long id, @RequestBody IrbDto.SurveyQuestionRequest req) {
        return ResponseEntity.ok(questionService.updateQuestion(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}