package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/irb-results")
@RequiredArgsConstructor
public class IrbSurveyResultController {

    private final IrbSurveyResultService resultService;

    @PostMapping
    public ResponseEntity<IrbDto.ResultResponse> submitResult(@RequestBody IrbDto.ResultRequest req) {
        return ResponseEntity.ok(resultService.submitReviewResult(req));
    }

    @GetMapping
    public ResponseEntity<List<IrbDto.ResultResponse>> getAllResults() {
        return ResponseEntity.ok(resultService.getAllResult());
    }

    @GetMapping("/test/{irbTestId}")
    public ResponseEntity<List<IrbDto.ResultResponse>> getResultsByTestId(@PathVariable Long irbTestId) {
        return ResponseEntity.ok(resultService.getResultByTestId(irbTestId));
    }

    @GetMapping("/my/{irbTestId}")
    public ResponseEntity<IrbDto.ResultResponse> getMyResult(@PathVariable Long irbTestId, @RequestParam Long memberId) {
        return ResponseEntity.ok(resultService.getMyResult(irbTestId, memberId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IrbDto.ResultResponse> update(@PathVariable Long id, @RequestBody IrbDto.ResultRequest req) {
        return ResponseEntity.ok(resultService.submitReviewResult(req));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        resultService.delete(id);
    }
}