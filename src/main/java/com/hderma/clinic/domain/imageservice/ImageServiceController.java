package com.hderma.clinic.domain.imageservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 조회(GET)는 사이트 방문자 누구나, 등록/수정/삭제는 AdminAuthFilter에서 관리자만 허용 */
@RestController
@RequestMapping("/api/image-services")
@RequiredArgsConstructor
public class ImageServiceController {

    private final ImageServiceService service;

    @GetMapping
    public List<ImageServiceDto.Response> list() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<ImageServiceDto.SaveResult> create(@RequestBody ImageServiceDto.Request req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImageServiceDto.SaveResult> update(@PathVariable Long id, @RequestBody ImageServiceDto.Request req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}