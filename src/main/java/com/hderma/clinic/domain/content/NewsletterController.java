package com.hderma.clinic.domain.content;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/newsletters")
@RequiredArgsConstructor
public class NewsletterController {

    private final NewsletterService service;

    @GetMapping
    public List<NewsletterDto.Response> list() { return service.findAll(); }

    @GetMapping("/{id}")
    public NewsletterDto.Response detail(@PathVariable Long id) { return service.findOne(id); }

    @PostMapping
    public ResponseEntity<NewsletterDto.SaveResult> create(@RequestBody NewsletterDto.Request req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsletterDto.SaveResult> update(@PathVariable Long id, @RequestBody NewsletterDto.Request req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}