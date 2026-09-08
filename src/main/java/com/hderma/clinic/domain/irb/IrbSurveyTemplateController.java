package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/irb-templates")
@RequiredArgsConstructor
public class IrbSurveyTemplateController {

    private final IrbSurveyTemplateService templateService;

    @PostMapping
    public ResponseEntity<IrbDto.SurveyTemplateResponse> createTemplate(@RequestBody IrbDto.SurveyTemplateRequest req) {
        return ResponseEntity.ok(templateService.createTemplate(req));
    }

    @GetMapping("/{irbTestId}")
    public ResponseEntity<IrbDto.SurveyTemplateResponse> getTemplate(@PathVariable Long irbTestId, @RequestParam String surveyName) {
        return ResponseEntity.ok(templateService.getTemplate(irbTestId, surveyName));
    }

    @GetMapping
    public ResponseEntity<List<IrbDto.SurveyTemplateResponse>> getAll() {
        return ResponseEntity.ok(templateService.getTemplatesAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<IrbDto.SurveyTemplateResponse> updateTemplate(@PathVariable Long id, @RequestBody IrbDto.SurveyTemplateRequest req) {
        return ResponseEntity.ok(templateService.updateTemplate(id, req));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        templateService.delete(id);
    }
}