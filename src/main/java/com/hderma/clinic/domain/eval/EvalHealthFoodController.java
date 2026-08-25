package com.hderma.clinic.domain.eval;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eval-health-food")
@RequiredArgsConstructor
public class EvalHealthFoodController {

    private final EvalHealthFoodService service;

    @GetMapping
    public List<EvalHealthFoodDto.Response> list() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<EvalHealthFoodDto.SaveResult> create(@RequestBody EvalHealthFoodDto.Request req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvalHealthFoodDto.SaveResult> update(@PathVariable Long id, @RequestBody EvalHealthFoodDto.Request req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}