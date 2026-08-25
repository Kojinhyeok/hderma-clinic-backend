package com.hderma.clinic.domain.recruitment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService service;

    @GetMapping
    public List<RecruitmentDto.Response> list(@RequestParam(required = false) String evalCategory) {
        return service.findAll(evalCategory);
    }

    @GetMapping("/{id}")
    public RecruitmentDto.Response detail(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody RecruitmentDto.Request req) {
        return ResponseEntity.ok(Map.of("id", service.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody RecruitmentDto.Request req) {
        service.update(id, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}