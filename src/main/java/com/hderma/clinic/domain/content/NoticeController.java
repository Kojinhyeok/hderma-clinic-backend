package com.hderma.clinic.domain.content;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService service;

    @GetMapping
    public List<NoticeDto.Response> list() { return service.findAll(); }

    @GetMapping("/{id}")
    public NoticeDto.Response detail(@PathVariable Long id) {
        return service.findOneAndIncreaseView(id);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody NoticeDto.Request req) {
        return ResponseEntity.ok(Map.of("id", service.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody NoticeDto.Request req) {
        service.update(id, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}