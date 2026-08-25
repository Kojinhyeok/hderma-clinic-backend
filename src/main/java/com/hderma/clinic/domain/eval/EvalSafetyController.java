package com.hderma.clinic.domain.eval;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eval-safety")
@RequiredArgsConstructor
public class EvalSafetyController {

    private final EvalSafetyService service;

    @GetMapping
    public List<EvalSafetyDto.Response> list() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<EvalSafetyDto.SaveResult> create(@RequestBody EvalSafetyDto.Request req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvalSafetyDto.SaveResult> update(@PathVariable Long id, @RequestBody EvalSafetyDto.Request req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}