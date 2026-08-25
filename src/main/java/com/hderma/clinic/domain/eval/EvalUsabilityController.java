package com.hderma.clinic.domain.eval;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eval-usability")
@RequiredArgsConstructor
public class EvalUsabilityController {

    private final EvalUsabilityService service;

    @GetMapping
    public List<EvalUsabilityDto.Response> list() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<EvalUsabilityDto.SaveResult> create(@RequestBody EvalUsabilityDto.Request req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvalUsabilityDto.SaveResult> update(@PathVariable Long id, @RequestBody EvalUsabilityDto.Request req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}