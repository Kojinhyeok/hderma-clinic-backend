package com.hderma.clinic.domain.eval;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eval-invitro")
@RequiredArgsConstructor
public class EvalInvitroController {

    private final EvalInvitroService service;

    @GetMapping
    public List<EvalInvitroDto.Response> list() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<EvalInvitroDto.SaveResult> create(@RequestBody EvalInvitroDto.Request req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvalInvitroDto.SaveResult> update(@PathVariable Long id, @RequestBody EvalInvitroDto.Request req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}