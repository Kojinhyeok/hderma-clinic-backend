package com.hderma.clinic.domain.eval;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eval-functional")
@RequiredArgsConstructor
public class EvalFunctionalController {

    private final EvalFunctionalService service;

    @GetMapping
    public List<EvalFunctionalDto.Response> list() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<EvalFunctionalDto.SaveResult> create(@RequestBody EvalFunctionalDto.Request req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvalFunctionalDto.SaveResult> update(@PathVariable Long id, @RequestBody EvalFunctionalDto.Request req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}