package com.hderma.clinic.domain.certification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CertificationMarkController {

    private final CertificationMarkService service;

    @GetMapping("/api/certification-marks")
    public List<CertificationMarkDto.MarkResponse> listMarks() {
        return service.findAllMarks();
    }

    @PostMapping("/api/certification-marks")
    public ResponseEntity<Map<String, Object>> createMark(@RequestBody CertificationMarkDto.MarkRequest req) {
        return ResponseEntity.ok(Map.of("id", service.createMark(req)));
    }

    @PutMapping("/api/certification-marks/{id}")
    public ResponseEntity<Void> updateMark(@PathVariable Long id, @RequestBody CertificationMarkDto.MarkRequest req) {
        service.updateMark(id, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/certification-marks/{id}")
    public ResponseEntity<Void> deleteMark(@PathVariable Long id) {
        service.deleteMark(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/certification-marks/{markId}/categories")
    public List<CertificationMarkDto.CategoryResponse> listCategories(@PathVariable Long markId) {
        return service.findCategoriesByMark(markId);
    }

    @PostMapping("/api/certification-marks/{markId}/categories")
    public ResponseEntity<CertificationMarkDto.SaveResult> createCategory(
            @PathVariable Long markId, @RequestBody CertificationMarkDto.CategoryRequest req) {
        return ResponseEntity.ok(service.createCategory(markId, req));
    }

    @DeleteMapping("/api/certification-mark-categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        service.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }
}